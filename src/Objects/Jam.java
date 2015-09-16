package Objects;

public class Jam 
{
	private int mId;
	private int mttl;
	private int mUserId;
	private User user;
	public Jam()
	{
		mId = 0;
		mttl = 0;
		mUserId = 0;
	}
	public Jam(int id, int ttl, int userid)
	{
		mId = id;
		mttl = ttl;
		mUserId = userid;
	}
	public Jam(int id)
	{
		mId = id;
		mttl = 0;
		mUserId = 0;
	}
	public void setUser(User u)
	{
		user = u;
	}
	public User getUser()
	{
		return user;
	}
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	public int getTtl() {
		return mttl;
	}
	public void setTtl(int mttl) {
		this.mttl = mttl;
	}
	public int getUserId() {
		return mUserId;
	}
	public void setUserId(int mUserId) {
		this.mUserId = mUserId;
	}
	public String toString()
	{
		return this.mId + ": " + this.user.getName() + "'s Jam";// "Jam ID: " + this.mId + ", User ID: " + this.mUserId + ", " + this.user.getName();
	}
}
