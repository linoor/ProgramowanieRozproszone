public enum CoordinateSystem {
	DIRECT {
		@Override
		public GameInterface.Course convert(GameInterface.Course c) {
			return c;
		}

		@Override
		public GameInterface.Position convert(GameInterface.Position p) {
			return new GameInterface.Position(p.getCol(), p.getRow());
		}
	},
	REVERSE {
		@Override
		public GameInterface.Course convert(GameInterface.Course c) {
			switch (c) {
			case WEST:
				return GameInterface.Course.EAST;
			case EAST:
				return GameInterface.Course.WEST;
			case NORTH:
				return GameInterface.Course.SOUTH;
			case SOUTH:
				return GameInterface.Course.NORTH;
			}
			return null;
		}

		@Override
		public GameInterface.Position convert(GameInterface.Position p) {
			return new GameInterface.Position(GameInterface.WIDTH - 1
					- p.getCol(), GameInterface.HIGHT - 1 - p.getRow());
		}

	};

	abstract public GameInterface.Course convert(GameInterface.Course c);

	abstract public GameInterface.Position convert(GameInterface.Position p);
}
