package API;

import java.util.ArrayList;

public class LastfmArtist {
	private String name;
	private String image;
	private int listeners;
	private int playcount;
	private ArrayList<LastfmTag> tags;
	private String bio;
	
	public LastfmArtist() {
		tags = new ArrayList<LastfmTag>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getListeners() {
		return listeners;
	}
	public void setListeners(int listeners) {
		this.listeners = listeners;
	}
	public int getPlaycount() {
		return playcount;
	}
	public void setPlaycount(int playcount) {
		this.playcount = playcount;
	}
	public ArrayList<LastfmTag> getTags() {
		return tags;
	}
	public void addTag(LastfmTag tag) {
		this.tags.add(tag);
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
}
