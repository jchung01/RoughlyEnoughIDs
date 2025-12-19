package org.dimdev.jeid.core.injectors;

import org.dimdev.jeid.core.JEIDLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

/**
 * An ASM-style injector to inject our biome array initialization code.
 * This is more flexible than the old mixin, improving compatibility with hybrid servers (Mohist, CatServer) and mods
 * that call {@link net.minecraft.world.gen.IChunkGenerator#generateChunk} in unexpected methods.
 */
public class ChunkBiomeInjector {
    private static final String GENERATE_CHUNK_METHOD = JEIDLoadingPlugin.isDeobf ? "generateChunk" : "func_185932_a";

    public static byte[] injectBiomeArrayInit(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        for (MethodNode method : cn.methods) {
            ListIterator<AbstractInsnNode> itr = method.instructions.iterator();
            while (itr.hasNext()) {
                AbstractInsnNode instruction = itr.next();
                if (instruction instanceof MethodInsnNode) {
                    MethodInsnNode methodNode = (MethodInsnNode) instruction;
                    if (methodNode.owner.equals("net/minecraft/world/gen/IChunkGenerator") &&
                            methodNode.name.equals(GENERATE_CHUNK_METHOD)) {
                        // Capture IChunkGenerator to local
                        int chunkGenLocalIndex = method.maxLocals;
                        copyChunkGenerator(method, itr, chunkGenLocalIndex);
                        // Copy Chunk
                        itr.add(new InsnNode(Opcodes.DUP));
                        // Get local IChunkGenerator
                        itr.add(new VarInsnNode(Opcodes.ALOAD, chunkGenLocalIndex));
                        // Call the hook
                        itr.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "org/dimdev/jeid/core/REIDHooks",
                                "initializeBiomeArray",
                                "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/gen/IChunkGenerator;)V",
                                false));
                        break;
                    }
                }
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private static void copyChunkGenerator(MethodNode method, ListIterator<AbstractInsnNode> itrOriginal, int index) {
        ListIterator<AbstractInsnNode> itr = method.instructions.iterator(itrOriginal.previousIndex());
        // Backtrack for IChunkGenerator
        while (itr.hasPrevious()) {
            AbstractInsnNode instruction = itr.previous();
            if (instruction instanceof FieldInsnNode) {
                FieldInsnNode fieldNode = (FieldInsnNode) instruction;
                // Copy IChunkGenerator to local
                if (fieldNode.desc.equals("Lnet/minecraft/world/gen/IChunkGenerator;")) {
                    itr.next();
                    itr.add(new InsnNode(Opcodes.DUP));
                    itr.add(new VarInsnNode(Opcodes.ASTORE, index));
                    return;
                }
            }
        }
    }

}
