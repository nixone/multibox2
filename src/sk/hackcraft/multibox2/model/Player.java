package sk.hackcraft.multibox2.model;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;

public interface Player
{
	public void init();
	public void close();
	
	public boolean isPlaying();
	public boolean hasActiveMultimedia();
	
	public int getPlaybackPosition();
	public MultimediaItem getActiveMultimedia();

	public void requestPlayingStateChange(boolean playing);
	public void requestActiveMultimediaSkip();
	
	public void registerPlayerEventListener(PlayerEventListener listener);
	public void unregisterListener(PlayerEventListener listener);
	
	public interface PlayerEventListener
	{
		public void update(MultimediaItem multimedia, int playbackPosition, boolean playing);
	}
}
