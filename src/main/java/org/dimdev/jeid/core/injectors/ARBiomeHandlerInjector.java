package org.dimdev.jeid.core.injectors;

import org.dimdev.jeid.JEIDLogger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * An ASM-style injector to inject biome array modification code into Advanced Rocketry.
 * This is more flexible than the old grouped injectors, improving compatibility with different versions of the mod
 * and forks (AR-Reworked).
 */
public class ARBiomeHandlerInjector {
    private static final String WORLD_CLASS = "Lnet/minecraft/world/World;";
    private static final String BLOCK_POS_CLASS = "Lnet/minecraft/util/math/BlockPos;";

    public static byte[] injectChangeBiome(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        int patchedCount = 0;

        for (MethodNode method : cn.methods) {
            if (!method.name.equals("changeBiome")) {
                continue;
            }
            ListIterator<AbstractInsnNode> itr = method.instructions.iterator();
            while (itr.hasNext()) {
                AbstractInsnNode instruction = itr.next();
                if (validateInjectionTarget(itr, instruction)) {
                    HookParams args = gatherParameters(method);

                    String readableMethodDesc = Arrays.stream(Type.getArgumentTypes(method.desc))
                            .map(Type::getClassName)
                            .collect(Collectors.joining(", "));
                    if (!args.isValid()) {
                        JEIDLogger.LOGGER.error("Failed to patch (Advanced Rocketry) method BiomeHandler#{}({}), please report to RoughlyEnoughIDs!",
                                method.name,
                                readableMethodDesc);
                        break;
                    }
                    JEIDLogger.LOGGER.info("Patching (Advanced Rocketry) method BiomeHandler#{}({})",
                            method.name,
                            readableMethodDesc);
                    patchedCount++;

                    itr.previous(); // Move before cast
                    // Load remaining arguments
                    itr.add(new VarInsnNode(Opcodes.ALOAD, args.world()));
                    itr.add(new VarInsnNode(Opcodes.ALOAD, args.pos()));
                    // Call the hook
                    itr.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "org/dimdev/jeid/core/REIDHooks",
                            "setBiomeId",
                            "(ILnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)I",
                            false));
                    break;
                }
            }
        }

        JEIDLogger.LOGGER.info("Patched {} methods in Advanced Rocketry's BiomeHandler!", patchedCount);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private static boolean validateInjectionTarget(ListIterator<AbstractInsnNode> itr, AbstractInsnNode instruction) {
        boolean validated = false;
        // Cast int id to byte
        if (instruction.getOpcode() == Opcodes.I2B) {
            // Store id into byte[]
            if (itr.next().getOpcode() == Opcodes.BASTORE) {
                validated = true;
            }
            itr.previous();
        }
        return validated;
    }

    private static HookParams gatherParameters(MethodNode method) {
        int worldIndex = -1;
        int blockPosIndex = -1;
        Type[] params = Type.getArgumentTypes(method.desc);

        for (int i = 0; i < params.length; i++) {
            Type param = params[i];
            switch (param.getDescriptor()) {
                case WORLD_CLASS:
                    worldIndex = i;
                    break;
                case BLOCK_POS_CLASS:
                    blockPosIndex = i;
                    break;
            }
        }
        return new HookParams(worldIndex, blockPosIndex);
    }

    private static class HookParams {
        private final int worldIndex;
        private final int blockPosIndex;

        HookParams(int worldIndex, int blockPosIndex) {
            this.worldIndex = worldIndex;
            this.blockPosIndex = blockPosIndex;
        }

        public int world() {
            return worldIndex;
        }

        public int pos() {
            return blockPosIndex;
        }

        public boolean isValid() {
            return worldIndex >= 0 && blockPosIndex >= 0;
        }
    }
}
