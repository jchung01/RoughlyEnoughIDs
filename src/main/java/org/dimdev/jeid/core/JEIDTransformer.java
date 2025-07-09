package org.dimdev.jeid.core;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.dimdev.jeid.JEIDLogger;
import org.dimdev.jeid.config.ConfigHandler;
import org.dimdev.jeid.util.ASMException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Since 2.1.0: Cleaned up obfuscated code; converted most transforms to mixins.
 * <p>
 * This class was borrowed from Zabi94's MaxPotionIDExtender
 * under MIT License and with full help of Zabi. All credit in this class goes to Zabi
 * and his incredible work on figuring out how to make this work and helping out.
 * <p>
 * <a href="https://github.com/zabi94/MaxPotionIDExtender">MaxPotionIDExtender by Zabi94</a>
 */
public class JEIDTransformer implements IClassTransformer {
    private final Map<String, Function<byte[], byte[]>> transformations;

    public JEIDTransformer() {
        transformations = new HashMap<>();

        addTransformation("net.minecraft.item.ItemStack", this::patchItemEnchantTag);
        addTransformation("net.minecraft.potion.PotionEffect", this::patchPotionTag);

        addTransformation("net.minecraft.world.gen.ChunkProviderServer", ChunkBiomeInjector::injectBiomeArrayInit);
        for (String extraTarget : ConfigHandler.TRANSFORMER.chunkBiomeInitPatchList) {
            addTransformation(extraTarget, ChunkBiomeInjector::injectBiomeArrayInit);
        }
    }

    public void addTransformation(String key, Function<byte[], byte[]> value) {
        JEIDLogger.LOGGER.info("Adding class {} to the transformation queue", key);
        transformations.put(key, value);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        Function<byte[], byte[]> transform = transformations.get(transformedName);
        if (transform != null) {
            return transform.apply(basicClass);
        }
        return basicClass;
    }

    private static MethodNode locateMethod(ClassNode cn, String desc, String... namesIn) {
        return cn.methods.stream()
                .filter(n -> n.desc.equals(desc) && anyMatch(namesIn, n.name))
                .findAny().orElseThrow(() -> new ASMException(getNames(namesIn) + ": " + desc + " cannot be found in " + cn.name, cn));
    }

    private static boolean anyMatch(String[] pool, String match) {
        for (String s : pool) {
            if (s.equals(match)) {
                return true;
            }
        }
        return false;
    }

    private static String getNames(String[] pool) {
        StringBuilder sb = new StringBuilder();
        for (String s : pool) {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private static AbstractInsnNode locateTargetInsn(MethodNode mn, Predicate<AbstractInsnNode> filter) {
        AbstractInsnNode target = null;
        Iterator<AbstractInsnNode> i = mn.instructions.iterator();
        while (i.hasNext() && target == null) {
            AbstractInsnNode n = i.next();
            if (filter.test(n)) {
                target = n;
            }
        }
        if (target == null) {
            throw new ASMException("Can't locate target instruction in " + mn.name, mn);
        }
        return target;
    }

    private byte[] patchItemEnchantTag(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        String enchantment = "net/minecraft/enchantment/Enchantment";
        String nbtTagCompound = "net/minecraft/nbt/NBTTagCompound";
        // Remove short cast
        MethodNode mn = locateMethod(cn, "(L" + enchantment + ";I)V", "addEnchantment", "func_77966_a");
        mn.instructions.remove(locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.I2S));

        // Redirect setShort to setInt
        AbstractInsnNode target2 = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && n.getPrevious().getPrevious().getPrevious().getOpcode() == Opcodes.LDC && ((LdcInsnNode) n.getPrevious().getPrevious().getPrevious()).cst.toString().equals("id"));
        mn.instructions.insertBefore(target2, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, nbtTagCompound, (JEIDLoadingPlugin.isDeobf ? "setInteger" : "func_74768_a"), "(Ljava/lang/String;I)V", false));
        mn.instructions.remove(target2);

        // Redirect getShort to getInt
        if (FMLLaunchHandler.side().isClient()) {
            MethodNode mn2 = locateMethod(cn, "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List;", "getTooltip", "func_82840_a");
            AbstractInsnNode target = locateTargetInsn(mn2, n -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && n.getPrevious().getOpcode() == Opcodes.LDC && ((LdcInsnNode) n.getPrevious()).cst.toString().equals("id"));
            mn.instructions.insertBefore(target, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, nbtTagCompound, (JEIDLoadingPlugin.isDeobf ? "getInteger" : "func_74762_e"), "(Ljava/lang/String;)I", false));
            mn.instructions.remove(target);
        }

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private byte[] patchPotionTag(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        String nbtTagCompound = "net/minecraft/nbt/NBTTagCompound";
        String potionEffect = "net/minecraft/potion/PotionEffect";
        MethodNode mn = locateMethod(cn, "(L" + nbtTagCompound + ";)L" + nbtTagCompound + ";", "writeCustomPotionEffectToNBT", "func_82719_a");
        AbstractInsnNode ant = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.I2B);
        MethodInsnNode call = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, nbtTagCompound, (JEIDLoadingPlugin.isDeobf ? "setInteger" : "func_74768_a"), "(Ljava/lang/String;I)V", false);
        // Redirect setByte to setInteger
        mn.instructions.remove(ant.getNext());
        mn.instructions.insert(ant, call);
        // Remove byte cast
        mn.instructions.remove(ant);

        MethodNode mn2 = locateMethod(cn, "(L" + nbtTagCompound + ";)L" + potionEffect + ";", "readCustomPotionEffectFromNBT", "func_82722_b");
        AbstractInsnNode ant2 = locateTargetInsn(mn2, n -> n.getOpcode() == Opcodes.INVOKEVIRTUAL);
        // Remove bitwise operation
        mn2.instructions.remove(ant2.getNext());
        mn2.instructions.remove(ant2.getNext());
        // Redirect getByte to getInteger
        mn2.instructions.insert(ant2, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, nbtTagCompound, (JEIDLoadingPlugin.isDeobf ? "getInteger" : "func_74762_e"), "(Ljava/lang/String;)I", false));
        mn2.instructions.remove(ant2);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }
}

