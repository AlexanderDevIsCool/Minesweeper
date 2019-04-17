package ua.games.minesweeper.service;

public enum GameMode {
    Beginner{
        @Override
        public int getHeight() {
            return 9;
        }

        @Override
        public int getWidth() {
            return 9;
        }

        @Override
        public int getMines() {
            return 10;
        }
    },
    Intermediate{
        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getMines() {
            return 40;
        }
    },
    Expert{
        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public int getWidth() {
            return 30;
        }

        @Override
        public int getMines() {
            return 99;
        }
    };

    public abstract int getHeight();
    public abstract int getWidth();
    public abstract int getMines();

}
