package elucent.albedo.asm;

import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nullable;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ASMTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.client.renderer.ChunkRenderContainer")) {
			return patchRenderChunkASM(name, basicClass, name.compareTo(transformedName) != 0);
		}
		return basicClass;
	}
	
	public byte[] patchRenderChunkASM(String name, byte[] bytes, boolean obfuscated){
		String renderChunkName = "";
		String targetMethod = "";
		if (obfuscated){
			targetMethod = "a";
			renderChunkName = "Lbte;";
		}
		else {
			targetMethod = "preRenderChunk";
			renderChunkName = "Lnet/minecraft/client/renderer/chunk/RenderChunk;";
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		List<MethodNode> methods = classNode.methods;
		
		for (MethodNode m : methods){
			if (m.name.compareTo(targetMethod) == 0 && m.desc.compareTo("("+renderChunkName+")V") == 0){
				InsnList code = m.instructions;
				List<LocalVariableNode> vars = m.localVariables;
				int paramloc = -1;
				for (int i = 0; i < vars.size(); i ++){
					LocalVariableNode p = vars.get(i);
					if (p.desc.compareTo(renderChunkName) == 0){
						paramloc = i;
					}
				}
                @Nullable AbstractInsnNode returnNode = null;
                for(ListIterator<AbstractInsnNode> iterator = code.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode insn = iterator.next();

                    if(insn.getOpcode() == Opcodes.RETURN) {
                        returnNode = insn;
                        break;
                    }
                }

                if(returnNode != null && paramloc > -1) {
                	code.insertBefore(returnNode, new VarInsnNode(Opcodes.ALOAD, paramloc));
                	MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "elucent/albedo/util/RenderUtil",
                            "renderChunkUniforms", "("+renderChunkName+")V", false);
                    code.insertBefore(returnNode, method);
                    System.out.println("Successfully loaded RenderChunk ASM!");
                }
                else {
                }
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
