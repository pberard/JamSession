package Objects;

public class User 
{
	private String mName;
	private String mEmail;
	private int mId;
	
	public User()
	{
		
	}
	public User(int id)
	{
		mId = id;
	}
	public User(String name, String email, int id)
	{
		mName = name;
		mEmail = email;
		mId = id;
	}
	public String getName() 
	{
		return mName;
	}
	public void setName(String mName) 
	{
		this.mName = mName;
	}
	public String getEmail() 
	{
		return mEmail;
	}
	public void setEmail(String mEmail) 
	{
		this.mEmail = mEmail;
	}
	public int getId() 
	{
		return mId;
	}
	public void setId(int mId) 
	{
		this.mId = mId;
	}
	public String toString()
	{
		return mName + ", " + mEmail;
	}
}
