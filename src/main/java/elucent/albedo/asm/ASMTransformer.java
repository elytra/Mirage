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
		if (transformedName.equals("net.minecraft.client.renderer.entity.RenderManager")) {
			return patchRenderManagerASM(name, basicClass, name.compareTo(transformedName) != 0);
		}
		if (transformedName.equals("net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher")) {
			return patchTERendererASM(name, basicClass, name.compareTo(transformedName) != 0);
		}
		if (transformedName.equals("net.minecraft.client.renderer.GlStateManager")) {
			return patchGlStateManagerASM(name, basicClass, name.compareTo(transformedName) != 0);
		}
		if (transformedName.equals("net.minecraft.profiler.Profiler")) {
			return patchProfilerASM(name, basicClass, name.compareTo(transformedName) != 0);
		}
		if (transformedName.compareTo("net.minecraft.client.renderer.RenderItem") == 0) {
			return patchRenderItemASM(name, basicClass, name.compareTo(transformedName) != 0);
		}
		if (transformedName.compareTo("net.minecraftforge.client.ForgeHooksClient") == 0) {
			return patchForgeHooksASM(name, basicClass, name.compareTo(transformedName) != 0);
		}
		return basicClass;
	}
	
	public byte[] patchForgeHooksASM(String name, byte[] bytes, boolean obfuscated) {
		String targetMethod = "";
		String transformTypeName = "";
		if (obfuscated){
			targetMethod = "handleCameraTransforms";
			transformTypeName = "Lbro$b;";
		} else {
			targetMethod = "handleCameraTransforms";
			transformTypeName = "Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;";
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		List<MethodNode> methods = classNode.methods;
		
		for (MethodNode m : methods) {
			if (m.name.compareTo(targetMethod) == 0) {
				InsnList code = m.instructions;
				List<LocalVariableNode> vars = m.localVariables;
				int paramloc = -1;
				if (!obfuscated) {
					paramloc = 1;
				}
				for (int i = 0; i < vars.size() && paramloc == -1; i ++){
					LocalVariableNode p = vars.get(i);
					if (p.desc.compareTo(transformTypeName) == 0){
						paramloc = i;
					}
				}

				if (paramloc > -1) {
					MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "elucent/albedo/util/RenderUtil",
							"setTransform", "("+transformTypeName+")V", false);
					code.insertBefore(code.get(2), method);
					code.insertBefore(code.get(2), new VarInsnNode(Opcodes.ALOAD, paramloc));
					//System.out.println("Successfully patched ForgeHooksClient!");
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] patchRenderItemASM(String name, byte[] bytes, boolean obfuscated) {
		String itemStackName = "";
		String bakedModelName = "";
		String targetMethod = "";
		String transformTypeName = "";
		if (obfuscated){
			targetMethod = "a";
			itemStackName = "Lafj;";
			bakedModelName = "Lcbh;";
		} else {
			targetMethod = "renderItem";
			itemStackName = "Lnet/minecraft/item/ItemStack;";
			bakedModelName = "Lnet/minecraft/client/renderer/block/model/IBakedModel;";
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		List<MethodNode> methods = classNode.methods;
		
		for (MethodNode m : methods){
			if (m.name.compareTo(targetMethod) == 0 && m.desc.compareTo("("+itemStackName+bakedModelName+")V") == 0){
				InsnList code = m.instructions;
				List<LocalVariableNode> vars = m.localVariables;
				int paramloc = -1;
				for (int i = 0; i < vars.size(); i ++){
					LocalVariableNode p = vars.get(i);
					if (p.desc.compareTo(itemStackName) == 0){
						paramloc = i;
					}
				}
				@Nullable AbstractInsnNode returnNode = null;
				ListIterator<AbstractInsnNode> iterator = code.iterator();
				while (iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if (insn.getOpcode() == Opcodes.RETURN) {
						returnNode = insn;
						break;
					}
				}

				if (returnNode != null && paramloc > -1) {
					code.insertBefore(returnNode, new VarInsnNode(Opcodes.ALOAD, paramloc));
					MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "elucent/albedo/util/RenderUtil",
							"renderItem", "("+itemStackName+")V", false);
					code.insertBefore(returnNode, method);
					//System.out.println("Successfully patched RenderItem!");
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] patchGlStateManagerASM(String name, byte[] bytes, boolean obfuscated) {
		String enableLighting = "";
		String disableLighting = "";
		String enableFog = "";
		String disableFog = "";
		if (obfuscated){
			enableLighting = "f";
			disableLighting = "g";
			enableLighting = "o";
			disableLighting = "p";
		} else {
			enableLighting = "enableLighting";
			disableLighting = "disableLighting";
			enableFog = "enableFog";
			disableFog = "disableFog";
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		List<MethodNode> methods = classNode.methods;
		
		for (MethodNode m : methods) {
			if (m.name.compareTo(enableLighting) == 0) {
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "elucent/albedo/util/RenderUtil",
						"enableLightingUniforms", "()V", false);
				code.insertBefore(code.get(2), method);
			}
			if (m.name.compareTo(disableLighting) == 0) {
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "elucent/albedo/util/RenderUtil",
						"disableLightingUniforms", "()V", false);
				code.insertBefore(code.get(2), method);
			}
		}
		//System.out.println("Successfully loaded GlStateManager ASM!");
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] patchTERendererASM(String name, byte[] bytes, boolean obfuscated) {
		String entityName = "";
		String targetMethod = "";
		String targetDesc = "";
		if (obfuscated){
			targetMethod = "a";
			entityName = "Lasc;";
			targetDesc = "(Lasc;FI)V";
		} else {
			targetMethod = "renderTileEntity";
			entityName = "Lnet/minecraft/tileentity/TileEntity;";
			targetDesc = "(Lnet/minecraft/tileentity/TileEntity;FI)V";
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		List<MethodNode> methods = classNode.methods;
		
		for (MethodNode m : methods) {
			if (m.name.compareTo(targetMethod) == 0 && m.desc.compareTo(targetDesc) == 0){
				InsnList code = m.instructions;
				List<LocalVariableNode> vars = m.localVariables;
				int paramloc = 1;
				@Nullable AbstractInsnNode returnNode = null;
				ListIterator<AbstractInsnNode> iterator = code.iterator();
				while (iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();

					if (insn.getOpcode() == Opcodes.RETURN) {
						returnNode = insn;
						break;
					}
				}

				if (returnNode != null && paramloc > -1) {
					MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "elucent/albedo/event/RenderTileEntityEvent",
							"postNewEvent", "("+entityName+")V", false);
					code.insertBefore(code.get(2), method);
					code.insertBefore(code.get(2), new VarInsnNode(Opcodes.ALOAD, paramloc));
					//System.out.println("Successfully loaded TileEntityRendererDispatcher ASM!");
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] patchRenderManagerASM(String name, byte[] bytes, boolean obfuscated) {
		String entityName = "";
		String entityFieldName = "";
		String targetMethod = "";
		String targetDesc = "";
		if (obfuscated){
			targetMethod = "a";
			entityName = "Lsn;";
			targetDesc = "(Lsn;DDDFFZ)V";
		} else {
			targetMethod = "doRenderEntity";
			entityName = "Lnet/minecraft/entity/Entity;";
			targetDesc = "(Lnet/minecraft/entity/Entity;DDDFFZ)V";
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		List<MethodNode> methods = classNode.methods;
		
		for (MethodNode m : methods) {
			if (m.name.compareTo(targetMethod) == 0 && m.desc.compareTo(targetDesc) == 0) {
				////System.out.println("Attempting to load RenderManager ASM...");
				InsnList code = m.instructions;
				List<LocalVariableNode> vars = m.localVariables;
				int paramloc = 1;
				@Nullable AbstractInsnNode returnNode = null;
				ListIterator<AbstractInsnNode> iterator = code.iterator();
				while (iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();

					if (insn.getOpcode() == Opcodes.RETURN) {
						returnNode = insn;
						break;
					}
				}

				if (returnNode != null && paramloc > -1) {
					MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "elucent/albedo/event/RenderEntityEvent",
							"postNewEvent", "("+entityName+")V", false);
					code.insertBefore(code.get(2), method);
					code.insertBefore(code.get(2), new VarInsnNode(Opcodes.ALOAD, paramloc));
					//System.out.println("Successfully loaded RenderManager ASM!");
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] patchProfilerASM(String name, byte[] bytes, boolean obfuscated) {
		String targetMethod = "";
		if (obfuscated){
			targetMethod = "a";
		} else {
			targetMethod = "endStartSection";
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		List<MethodNode> methods = classNode.methods;
		
		for (MethodNode m : methods) {
			if (m.name.compareTo(targetMethod) == 0 && m.desc.compareTo("(Ljava/lang/String;)V") == 0){
				InsnList code = m.instructions;
				List<LocalVariableNode> vars = m.localVariables;
				int paramloc = -1;
				//if (!obfuscated){
					paramloc = 1;
				/*}
				for (int i = 0; i < vars.size() && paramloc == -1; i ++){
					LocalVariableNode p = vars.get(i);
					if (p.desc.compareTo("Ljava/lang/String;") == 0){
						paramloc = i;
					}
				}*/
				@Nullable AbstractInsnNode returnNode = null;
				ListIterator<AbstractInsnNode> iterator = code.iterator();
				while (iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();

					if (insn.getOpcode() == Opcodes.RETURN) {
						returnNode = insn;
						break;
					}
				}

				if (paramloc > -1) {
					MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "elucent/albedo/event/ProfilerStartEvent",
							"postNewEvent", "(Ljava/lang/String;)V", false);
					code.insertBefore(code.get(2), method);
					code.insertBefore(code.get(2), new VarInsnNode(Opcodes.ALOAD, paramloc));
					//System.out.println("Successfully loaded Profiler ASM!");
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] patchRenderChunkASM(String name, byte[] bytes, boolean obfuscated) {
		String renderChunkName = "";
		String targetMethod = "";
		if (obfuscated){
			targetMethod = "a";
			renderChunkName = "Lbte;";
		} else {
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
				if (!obfuscated) {
					paramloc = 1;
				}
				for (int i = 0; i < vars.size() && paramloc == -1; i ++){
					LocalVariableNode p = vars.get(i);
					if (p.desc.compareTo(renderChunkName) == 0){
						paramloc = i;
					}
				}
				@Nullable AbstractInsnNode returnNode = null;
				ListIterator<AbstractInsnNode> iterator = code.iterator();
				while (iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();

					if(insn.getOpcode() == Opcodes.RETURN) {
						returnNode = insn;
						break;
					}
				}

				if (returnNode != null && paramloc > -1) {
					code.insertBefore(returnNode, new VarInsnNode(Opcodes.ALOAD, paramloc));
					MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "elucent/albedo/util/RenderUtil",
							"renderChunkUniforms", "("+renderChunkName+")V", false);
					code.insertBefore(returnNode, method);
					//System.out.println("Successfully loaded RenderChunk ASM!");
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
