package sk.hackcraft.multibox2.android.client;

public interface BackPressedEvent
{
	public void registerBackPressedListener(BackPressedListener listener);
	public void unregisterBackPressedListener(BackPressedListener listener);
}
