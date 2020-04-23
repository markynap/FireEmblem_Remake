package extras;

import java.util.ArrayList;

public class MusicPlayer implements Runnable {

	private ArrayList<AudioFile> musicFiles;
	private int currentSongIndex;
	private boolean running;
	public AudioFile song;
	public AudioFile tempSong;
	
	public MusicPlayer(String...files) {
		musicFiles = new ArrayList<>();
		currentSongIndex = 0;
		for (String file : files) {
			musicFiles.add(new AudioFile("./res/" + file+ ".wav"));
		}
		song = musicFiles.get(currentSongIndex);
		running = true;
	}
	/*
	@Override
	public void run() {
		running = true;
		song = musicFiles.get(currentSongIndex);
		song.play();
		while (running) {
			if (!song.isPlaying()) {
				currentSongIndex++;
				if (currentSongIndex >= musicFiles.size()) {
					currentSongIndex = 0;
				}
				song = musicFiles.get(currentSongIndex);
				song.play();
			}
		}
	
	}*/
	
	public void run() {
		if (song == null) return;
		//song.play();
		//while (running) if (!song.isPlaying()) song.play();
	}
	
	public void nextSong() {
		song.stop();
	//	currentSongIndex++;
	//	if (currentSongIndex >= musicFiles.size()) {
	//		currentSongIndex = 0;
	//	}
	}
	
	public void playSong(int index) {
		song.stop();
		song = musicFiles.get(index);
		song.play();
		running = true;
	}

	public void playSong(String fileName) {
		int songindex = -1;
		for (int i = 0; i < musicFiles.size(); i++) {
			tempSong = musicFiles.get(i);
			if (tempSong.fileName.equalsIgnoreCase(fileName)) {
				songindex = i;
				break;
			}
		}
		if (songindex >= 0) {
			currentSongIndex = songindex-1;
			song.stop();
		}
	}
}
