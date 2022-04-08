package xyz.awesomenetwork.skyroyale.leaderboard;

public class SkyRoyaleLeaderboard {
	private final String[] leaderboard;

	public SkyRoyaleLeaderboard(int size) {
		this.leaderboard = new String[size];
		for (int i = 0; i < size; i++) leaderboard[i] = null; // Initialise every index
	}

	public int getSize() {
		return leaderboard.length;
	}

	public boolean setPosition(int position, String playerName) {
		if (position >= leaderboard.length) return false;
		leaderboard[position] = playerName;
		return true;
	}

	public String getPosition(int position) {
		return leaderboard[position];
	}

	public String[] getLeaderboard() {
		return leaderboard;
	}
}
