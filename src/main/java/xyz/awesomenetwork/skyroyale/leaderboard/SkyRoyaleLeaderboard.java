package xyz.awesomenetwork.skyroyale.leaderboard;

public class SkyRoyaleLeaderboard {
	private final String[] leaderboard;

	public SkyRoyaleLeaderboard(int size) {
		this.leaderboard = new String[size];
	}

	public int getSize() {
		return leaderboard.length;
	}

	public void setPosition(int position, String playerName) {
		leaderboard[position] = playerName;
	}

	public String getPosition(int position) {
		return leaderboard[position];
	}

	public String[] getLeaderboard() {
		return leaderboard;
	}
}
