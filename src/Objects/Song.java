package Objects;

public class Song 
{
	private int mId;
	private String mFilepath;
	private String mDropboxFilepath;
	//private int mLength;
	private String mDescription;
	private int mUserId;
	private int mJamId;
	public Song()
	{
		
	}
	public Song(int id, String filepath, int userId, int jamId)
	{
		mId = id;
		mFilepath = filepath;
		mUserId = userId;
		mJamId = jamId;
	}
	public Song(int id)
	{
		mId = id;
	}
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	public String getFilepath() {
		return mFilepath;
	}
	public void setFilepath(String mFilepath) {
		this.mFilepath = mFilepath;
	}
	public int getUserId() {
		return mUserId;
	}
	public void setUserId(int mUserId) {
		this.mUserId = mUserId;
	}
	public int getJamId() {
		return mJamId;
	}
	public void setJamId(int mJamId) {
		this.mJamId = mJamId;
	}
}
