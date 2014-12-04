package sk.hackcraft.multibox2.net.host;

public interface Coder<IN, OUT>
{
	public OUT encode(IN input);
	public IN decode(OUT output);
}
