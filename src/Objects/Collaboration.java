package Objects;

public class Collaboration 
{
	private int mId;
	private int mJamId;
	private int mUserId;
	public Collaboration()
	{
		
	}
	public Collaboration(int id, int jamId, int userId)
	{
		mId = id;
		mJamId = jamId;
		mUserId = userId;
	}
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	public int getJamId() {
		return mJamId;
	}
	public void setJamId(int mJamId) {
		this.mJamId = mJamId;
	}
	public int getUserId() {
		return mUserId;
	}
	public void setUserId(int mUserId) {
		this.mUserId = mUserId;
	}
}
