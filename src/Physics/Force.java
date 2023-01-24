package Physics;


public interface Force
{
	public void turnOn();
	public void turnOff();
	public boolean isOn();
	public boolean isOff();
	public void apply();
}