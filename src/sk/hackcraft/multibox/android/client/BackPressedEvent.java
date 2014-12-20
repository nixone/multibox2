package sk.hackcraft.multibox.android.client;

public interface BackPressedEvent
{
	public void registerBackPressedListener(BackPressedListener listener);
	public void unregisterBackPressedListener(BackPressedListener listener);
}
