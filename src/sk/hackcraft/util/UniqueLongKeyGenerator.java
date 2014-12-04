package sk.hackcraft.util;

public class UniqueLongKeyGenerator implements KeyGenerator<Long>
{
	private long nextFree;
	private long firstKey;
	
	public UniqueLongKeyGenerator(Long firstKey) {
		this.firstKey = this.nextFree = firstKey;
	}
	
	@Override
	public Long generateKey()
	{
		long result = nextFree++;
		if(nextFree == firstKey) {
			throw new RuntimeException("Unique long key generator ran out of numbers");
		}
		return result;
	}
}
