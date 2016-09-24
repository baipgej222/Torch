package org.torch;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.google.common.collect.Lists;

import net.minecraft.server.WorldServer;

public class TerrainGeneratingTask extends RecursiveAction {
	private static final long serialVersionUID = 1;
	WorldServer worldserver;
	List<int[]> positions;

	public TerrainGeneratingTask(final WorldServer worldserver, final List<int[]> position) {
		this.worldserver = worldserver;
		this.positions = position;
	}

	@Override
	protected void compute() {
		final List<RecursiveAction> forks = Lists.newLinkedList();
		positions.parallelStream().forEach(position -> {
			Generate task = new Generate(worldserver, position);
			forks.add(task);
			ForkJoinPool.commonPool().execute(task);
		});
	}

	private final class Generate extends RecursiveAction {
		private static final long serialVersionUID = 1;
		WorldServer worldserver;
		int[] positions;

		public Generate(final WorldServer worldserver, final int[] positions) {
			this.worldserver = worldserver;
			this.positions = positions;
		}

		@Override
		protected void compute() {
			worldserver.getChunkProviderServer().getChunkAt(positions[0], positions[1]);
		}

	}
}