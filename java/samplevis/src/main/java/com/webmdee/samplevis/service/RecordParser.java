package com.webmdee.samplevis.service;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.webmdee.samplevis.model.Album;
import com.webmdee.samplevis.model.Artist;
import com.webmdee.samplevis.model.Genre;
import com.webmdee.samplevis.model.Sample;
import com.webmdee.samplevis.model.SampledArtist;
import com.webmdee.samplevis.model.SampledArtistYearCount;
import com.webmdee.samplevis.model.Track;

/**
 * Parses in a CSV file supplied as args[0] containing information about sampled tracks from WhoSampled.com
 * The CSV has 11 columns per row:
 * 0 - Sampled artist name
 * 1 - Sampled track title
 * 2 - Sampled track album title
 * 3 - Sampled track year
 * 4 - Sampled track genre
 * 5 - Sampling artist name
 * 6 - Sampling track title
 * 7 - Sampling track album title
 * 8 - Sampling track year
 * 9 - Sampling track sample type
 * 10 - Sampling track part sampled
 * 11 - Sampling track whoSampled URL
 * After parsing in all of these rows into a collection of genres, artists, albums, tracks, and samples,
 * a SQLite database is created with those tables for that information.
 * After the records are entered, duplicates are removed and then two JSON files are written for D3:
 * 1 - A JSON file that contains a list of all the sampled artists and how many times they have been sampled
 * 2 - A JSON file that lists each year and how many times that year a sampled artist was sampled
 * 
 * The output of this is a DB file, and two JSON files that will support the treemap visualization as well as the linegraph
 * @author MDee
 *
 */
public class RecordParser {
	public static void main(String[] args) {
		Map<String, Artist> artistNameMap = new HashMap<String, Artist>();
		Map<String, Album> albumTitleMap = new HashMap<String, Album>();
		Map<String, Track> trackTitleMap = new HashMap<String, Track>();
		Map<String, Genre> genreNameMap = new HashMap<String, Genre>();

		Set<Artist> allArtists = new HashSet<Artist>();
		Set<Album> allAlbums = new HashSet<Album>();
		Set<Track> allTracks = new HashSet<Track>();
		Set<Sample> allSamples = new HashSet<Sample>();
		Set<Genre> allGenres = new HashSet<Genre>();
		
		// The path to the SQLite DB file that will be created and read from
		final String DB_PATH ="/Users/MDee/Desktop/data.db";
		// The path to the JSON file which will contain dictionaries of sampled artists, indicating their name and how many times they have been sampled
		final String SAMPLED_ARTISTS_JSON_PATH = "/Users/MDee/Desktop/sampledArtists.json";
		/* The path to the JSON file which will contain arrays of dictionaries.  Each array is indexed by the artist ID, and then it will contain 
		 * multiple dictionaries for each year that the artist was sampled with year and count keys.
		 */
		final String YEARS_ARTISTS_SAMPLED_PATH = "/Users/MDee/Desktop/yearsArtSampled.json";
		
		// indices to assign unique IDs
		int artistIdIndex = 1;
		int albumIdIndex = 1;
		int genreIdIndex = 1;
		int trackIdIndex = 1;
		int sampleIdIndex = 1;
		String filepath = args[0];
		String[] data;
		try {
			FileInputStream fstream = new FileInputStream(filepath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in),1);

			for (String line = br.readLine(); line != null; line = br.readLine()) {
				data = line.split(";");
				/*
				 * 0 - Sampled artist name
				 * 1 - Sampled track title
				 * 2 - Sampled track album title
				 * 3 - Sampled track year
				 * 4 - Sampled track genre
				 * 5 - Sampling artist name
				 * 6 - Sampling track title
				 * 7 - Sampling track album title
				 * 8 - Sampling track year
				 * 9 - Sampling track sample type
				 * 10 - Sampling track part sampled
				 * 11 - Sampling track whoSampled URL
				 */
				String sampledArtistName = data[0];
				String samplingArtistName = data[5];
				String sampledAlbumTitle = data[2];
				String samplingAlbumTitle = data[7];
				String sampledTrackTitle = data[1];
				int sampledTrackYear = Integer.parseInt(data[3]);
				String sampledTrackGenreName = data[4];
				String samplingTrackTitle = data[6];
				int samplingTrackYear = Integer.parseInt(data[8]);
				char samplingTrackSampleType = data[9].charAt(0);
				char samplingTrackPartSampled = data[10].charAt(0);
				String whoSampledUrl = data[11];

				// Noticed some releases do not give an album title, this is a default value for those cases
				samplingAlbumTitle = (samplingAlbumTitle.isEmpty()) ? "N/A" : samplingAlbumTitle;
				sampledAlbumTitle = (sampledAlbumTitle.isEmpty()) ? "N/A" : sampledAlbumTitle;

				/*
				 * Begin artist section
				 */
				Artist sampledArtist = null;
				// Create a test artist to see if the map already contains this artist
				Artist testSampledArtist = new Artist(artistIdIndex+1, sampledArtistName);
				if (artistNameMap.get(sampledArtistName) != null && artistNameMap.get(sampledArtistName).equals(testSampledArtist)) {
					sampledArtist = artistNameMap.get(sampledArtistName);
				} else {
					sampledArtist = new Artist(artistIdIndex++, sampledArtistName);
					allArtists.add(sampledArtist);
					artistNameMap.put(sampledArtistName, sampledArtist);
				}
				Artist samplingArtist = null;
				Artist testSamplingArtist = new Artist(artistIdIndex+1, samplingArtistName);
				if (artistNameMap.get(samplingArtistName) != null && artistNameMap.get(samplingArtistName).equals(testSamplingArtist)) {
					samplingArtist = artistNameMap.get(samplingArtistName);
				} else {
					samplingArtist = new Artist(artistIdIndex++, samplingArtistName);
					allArtists.add(samplingArtist);
					artistNameMap.put(samplingArtistName, samplingArtist);
				}
				/*
				 * End artist section
				 */

				/*
				 * Begin album section
				 */
				Album sampledArtistAlbum = null;
				Album testSampledArtistAlbum = new Album(albumIdIndex+1,sampledAlbumTitle,sampledArtist.getId(), sampledTrackYear);
				if (albumTitleMap.get(sampledAlbumTitle) != null && albumTitleMap.get(sampledAlbumTitle).equals(testSampledArtistAlbum)) {
					sampledArtistAlbum = albumTitleMap.get(sampledAlbumTitle);	
				} else {
					sampledArtistAlbum = new Album(albumIdIndex++, sampledAlbumTitle, sampledArtist.getId(), sampledTrackYear);
					allAlbums.add(sampledArtistAlbum);
					albumTitleMap.put(sampledAlbumTitle, sampledArtistAlbum);
				}
				Album samplingArtistAlbum = null;
				Album testSamplingArtistAlbum = new Album(albumIdIndex+1,samplingAlbumTitle,samplingArtist.getId(), samplingTrackYear);
				if (albumTitleMap.get(samplingAlbumTitle) != null && albumTitleMap.get(samplingAlbumTitle).equals(testSamplingArtistAlbum)) {
					samplingArtistAlbum = albumTitleMap.get(samplingAlbumTitle);
				} else {
					samplingArtistAlbum = new Album(albumIdIndex++, samplingAlbumTitle, samplingArtist.getId(), samplingTrackYear);
					allAlbums.add(samplingArtistAlbum);
					albumTitleMap.put(samplingAlbumTitle, samplingArtistAlbum);
				}
				/*
				 * End album section
				 */

				/*
				 * Begin genre section
				 */
				Genre sampledTrackGenre = null;
				Genre testSampledTrackGenre = new Genre(genreIdIndex+1,sampledTrackGenreName);
				if (genreNameMap.get(sampledTrackGenreName) != null && genreNameMap.get(sampledTrackGenreName).equals(testSampledTrackGenre)) {
					sampledTrackGenre = genreNameMap.get(sampledTrackGenreName);
				} else {
					sampledTrackGenre = new Genre(genreIdIndex++, sampledTrackGenreName);
					allGenres.add(sampledTrackGenre);
					genreNameMap.put(sampledTrackGenreName, sampledTrackGenre);
				}
				/*
				 * End genre section
				 */

				/*
				 * Begin track section
				 */
				// Need to construct two tracks, one for the sampled track and one for the sampled
				Track sampledTrack = null;
				Track testSampledTrack = new Track(trackIdIndex+1,sampledTrackTitle,sampledArtist.getId(),sampledArtistAlbum.getId(),sampledTrackYear);
				if (trackTitleMap.get(sampledTrackTitle) != null && trackTitleMap.get(sampledTrackTitle).equals(testSampledTrack)) {
					sampledTrack = trackTitleMap.get(sampledTrackTitle);
				} else {
					sampledTrack = new Track(trackIdIndex++,sampledTrackTitle,sampledArtist.getId(),sampledArtistAlbum.getId(),sampledTrackYear);
					sampledTrack.setGenreId(sampledTrackGenre.getId());
					trackTitleMap.put(sampledTrackTitle, sampledTrack);
					allTracks.add(sampledTrack);
				}
				Track samplingTrack = null;
				Track testSamplingTrack = new Track(trackIdIndex+1,samplingTrackTitle,samplingArtist.getId(),samplingArtistAlbum.getId(),samplingTrackYear);
				if (trackTitleMap.get(samplingTrackTitle) != null && trackTitleMap.get(samplingTrackTitle).equals(testSamplingTrack)) {
					samplingTrack = trackTitleMap.get(samplingTrackTitle);
				} else {
					samplingTrack = new Track(trackIdIndex++,samplingTrackTitle,samplingArtist.getId(),samplingArtistAlbum.getId(),samplingTrackYear);
					// Sampling tracks do not have a genre associated with them :(
					samplingTrack.setGenreId(-1);
					trackTitleMap.put(samplingTrackTitle, samplingTrack);
					allTracks.add(samplingTrack);
				}
				/*
				 * End track section
				 */

				/*
				 * Create sample record
				 */
				Sample sampleRecord = new Sample(sampleIdIndex++, sampledTrack.getId(), samplingTrack.getId(), samplingTrackSampleType, samplingTrackPartSampled, whoSampledUrl);
				allSamples.add(sampleRecord);
			}
			in.close();
			System.out.println("Done parsing in CSV");
			Class.forName("org.sqlite.JDBC");
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			conn.setAutoCommit(false);
			Statement stat = conn.createStatement();
			// Create artists table
			stat.executeUpdate("DROP TABLE IF EXISTS artists");
			stat.executeUpdate("CREATE TABLE artists (id INTEGER PRIMARY KEY NOT NULL, name VARCHAR(150) NOT NULL);");
			// Insert artist records
			PreparedStatement prepStat = conn.prepareStatement("INSERT INTO artists VALUES (?, ?);");
			Set<Artist> orderedArtists = new TreeSet<Artist>(allArtists);
			for (Artist a : orderedArtists) {
				prepStat.setInt(1, a.getId());
				prepStat.setString(2, a.getName());
				int colsAdded = prepStat.executeUpdate();
				assert(colsAdded == 1);
			}
			System.out.println("Done creating artists table");
			// Create albums table
			stat.executeUpdate("DROP TABLE IF EXISTS albums");
			stat.executeUpdate("CREATE TABLE albums (id INTEGER PRIMARY KEY NOT NULL, title VARCHAR(150) NOT NULL, artist_id INTEGER NOT NULL, year SMALLINT NOT NULL, FOREIGN KEY(artist_id) REFERENCES artists(id));");
			// Insert album records
			prepStat = conn.prepareStatement("INSERT INTO albums VALUES (?, ?, ?, ?);");
			Set<Album> orderedAlbums = new TreeSet<Album>(allAlbums);
			for (Album a : orderedAlbums) {
				prepStat.setInt(1, a.getId());
				prepStat.setString(2, a.getTitle());
				prepStat.setInt(3, a.getArtistId());
				prepStat.setInt(4, a.getYear());
				int colsAdded = prepStat.executeUpdate();
				assert(colsAdded == 1);
			}
			System.out.println("Done creating albums table");
			// Create genres table
			stat.executeUpdate("DROP TABLE IF EXISTS genres");
			stat.executeUpdate("CREATE TABLE genres (id SMALLINT PRIMARY KEY NOT NULL, name VARCHAR(50) NOT NULL);");
			// Insert genre records
			prepStat = conn.prepareStatement("INSERT INTO genres VALUES (?, ?);");
			Set<Genre> orderedGenres = new TreeSet<Genre>(allGenres);
			for (Genre g : orderedGenres) {
				prepStat.setInt(1, g.getId());
				prepStat.setString(2, g.getName());
				int colsAdded = prepStat.executeUpdate();
				assert(colsAdded == 1);
			}
			System.out.println("Done creating genres table");
			// Create tracks table
			stat.executeUpdate("DROP TABLE IF EXISTS tracks");
			stat.executeUpdate("CREATE TABLE tracks (id INTEGER PRIMARY KEY NOT NULL, title VARCHAR(200) NOT NULL, year SMALLINT NOT NULL, genre_id SMALLINT NOT NULL, album_id INTEGER NOT NULL, artist_id INTEGER NOT NULL, FOREIGN KEY(genre_id) REFERENCES genres(id), FOREIGN KEY(album_id) REFERENCES albums(id), FOREIGN KEY(artist_id) REFERENCES artists(id));");
			// Insert track records
			prepStat = conn.prepareStatement("INSERT INTO tracks VALUES (?, ?, ?, ?, ?, ?);");
			Set<Track> orderedTracks = new TreeSet<Track>(allTracks);
			for (Track t : orderedTracks) {
				prepStat.setInt(1, t.getId());
				prepStat.setString(2, t.getTitle());
				prepStat.setInt(3, t.getYear());
				prepStat.setInt(4, t.getGenreId());
				prepStat.setInt(5, t.getAlbumId());
				prepStat.setInt(6, t.getArtistId());
				int colsAdded = prepStat.executeUpdate();
				assert(colsAdded == 1);
			}
			System.out.println("Done creating tracks table");
			// Create samples table
			stat.executeUpdate("DROP TABLE IF EXISTS samples");
			stat.executeUpdate("CREATE TABLE samples (id INTEGER PRIMARY KEY NOT NULL, sampled_id INTEGER NOT NULL, sampler_id INTEGER NOT NULL, sample_type VARCHAR(1) NOT NULL, part_sampled VARCHAR(1) NOT NULL, who_sampled_url VARCHAR(300) NOT NULL, FOREIGN KEY(sampled_id) REFERENCES tracks(id), FOREIGN KEY(sampler_id) REFERENCES tracks(id));");
			// Insert sample records
			prepStat = conn.prepareStatement("INSERT INTO samples VALUES (?, ?, ?, ?, ?, ?);");
			Set<Sample> orderedSamples = new TreeSet<Sample>(allSamples);
			for (Sample s : orderedSamples) {
				prepStat.setInt(1, s.getId());
				prepStat.setInt(2, s.getSampledId());
				prepStat.setInt(3, s.getSamplingId());
				prepStat.setString(4, new Character(s.getSampleType()).toString());
				prepStat.setString(5, new Character(s.getSamplePart()).toString());
				prepStat.setString(6, s.getWhoSampledURL());
				int colsAdded = prepStat.executeUpdate();
				assert(colsAdded == 1);
			}
			System.out.println("Done creating samples table");
			// Update DB
			conn.commit();
			System.out.println("Done with initial data import into DB");
			/*
			 * There are some issues with duplication of albums. The method of putting records into a HashMap
			 * does not always work at detecting if this album had been seen before in the case of two different albums with the same title.
			 * Example: suppose you have two albums called "Foo" by artists A and B.  Say you encounter a record for B's album Foo.  You check
			 * the HashMap for an album that matches this title, but the record for A's album is returned.  Since they do not match, you add a duplicate
			 * for B's album Foo.  To address this you're going to iterate through the albums 
			 * that have the same title, year, and artist ID then merge them and update the tracks
			 */
			// Queries for retrieval, updating, and deletion
			String dupAlbQuery = "SELECT albums.title, COUNT(albums.title), albums.artist_id, COUNT(albums.artist_id), albums.year, COUNT(albums.year) FROM albums GROUP BY albums.title, albums.artist_id, albums.year HAVING (COUNT(albums.title) > 1) AND (COUNT(albums.artist_id) > 1) AND (COUNT(albums.year) > 1) ORDER BY albums.title ASC;";
			String minAlbIdQuery = "SELECT MIN(id) AS id FROM albums WHERE title=? AND year=? AND artist_id = ?;";
			String otherAlbIdQuery = "SELECT id FROM albums WHERE title=? AND year=? AND artist_id = ? AND id != ?;";
			String updateQuery = "UPDATE tracks SET album_id=? WHERE tracks.album_id =?;";
			String deleteQuery = "DELETE FROM albums WHERE id=?";
			// Find duplicate album records
			ResultSet albumResults = stat.executeQuery(dupAlbQuery);
			while (albumResults.next()) {
				String albumTitle = albumResults.getString("title");
				int albumYear = albumResults.getInt("year");
				int artistId = albumResults.getInt("artist_id");
				// Find the minimum album ID that matches this title and year
				prepStat = conn.prepareStatement(minAlbIdQuery);
				prepStat.setString(1, albumTitle);
				prepStat.setInt(2, albumYear);
				prepStat.setInt(3, artistId);
				ResultSet minIdSet = prepStat.executeQuery();
				int minAlbumId = minIdSet.getInt("id");
				// Now find the other album IDs that match title and year, and update every track linked to those with the min ID
				prepStat= conn.prepareStatement(otherAlbIdQuery);
				prepStat.setString(1, albumTitle);
				prepStat.setInt(2, albumYear);
				prepStat.setInt(3, artistId);
				prepStat.setInt(4, minAlbumId);
				ResultSet otherIdSet = prepStat.executeQuery();
				while (otherIdSet.next()) {
					// Now update tracks that use one of these other album IDs to use the min ID
					int oldAlbumId = otherIdSet.getInt("id");
					prepStat = conn.prepareStatement(updateQuery);
					prepStat.setInt(1, minAlbumId);
					prepStat.setInt(2, oldAlbumId);
					prepStat.executeUpdate();
					// Now delete those records that have old album IDs
					prepStat = conn.prepareStatement(deleteQuery);
					prepStat.setInt(1, oldAlbumId);
					int deleteCount = prepStat.executeUpdate();
					assert(deleteCount == 1);
				}
				otherIdSet.close();
			}
			albumResults.close();
			System.out.println("Done removing duplicate album titles");
			/*
			 * The same issue is happening with track titles
			 * Pretty much the same procedure will be followed here, but 
			 * you'll be updating sample records with the min ID and deleting from tracks
			 */
			String dupTrackQuery = "SELECT title, COUNT(title), album_id, COUNT(album_id), artist_id, COUNT(artist_id), year, COUNT(year), genre_id, COUNT(genre_id) FROM tracks GROUP BY title, album_id, artist_id, year, genre_id HAVING (COUNT(title) > 1) AND (COUNT(album_id) > 1) AND (COUNT(artist_id) > 1) AND (COUNT(year) > 1) AND (COUNT(genre_id) > 1) ORDER BY title ASC;";
			String minTrackIdQuery = "SELECT MIN(id) AS id FROM tracks WHERE title=? AND album_id=? AND artist_id=? AND year=?;";
			String otherTrackIdQuery = "SELECT id FROM tracks WHERE title=? AND album_id=? AND artist_id=? AND year=? AND genre_id=? AND id != ?;";
			String updateSampledIdQuery = "UPDATE samples SET sampled_id = ? WHERE sampled_id = ?;";
			String updateSamplingIdQuery = "UPDATE samples SET sampler_id = ? WHERE sampler_id = ?;";
			String deleteTrackIdQuery = "DELETE FROM tracks WHERE id=?";
			// Find all duplicate track records
			ResultSet trackResults = stat.executeQuery(dupTrackQuery);
			while (trackResults.next()) {
				String title = trackResults.getString("title");
				int albumId = trackResults.getInt("album_id");
				int artistId = trackResults.getInt("artist_id");
				int year = trackResults.getInt("year");
				int genreId = trackResults.getInt("genre_id");
				// Find minimum track Id amongst this set of duplicates
				prepStat = conn.prepareStatement(minTrackIdQuery);
				prepStat.setString(1, title);
				prepStat.setInt(2, albumId);
				prepStat.setInt(3, artistId);
				prepStat.setInt(4, year);
				ResultSet minIdResult = prepStat.executeQuery();
				int minTrackId = minIdResult.getInt("id");
				// Now find duplicates with the same title, album ID, artist ID, year, and genre ID 
				prepStat = conn.prepareStatement(otherTrackIdQuery);
				prepStat.setString(1, title);
				prepStat.setInt(2, albumId);
				prepStat.setInt(3, artistId);
				prepStat.setInt(4, year);
				prepStat.setInt(5, genreId);
				prepStat.setInt(6, minTrackId);
				ResultSet dupIdSet = prepStat.executeQuery();
				while (dupIdSet.next()) {
					int dupId = dupIdSet.getInt("id");
					/*
					 * Slight trickiness: you need to update the samples table but 
					 * the previous query returns duplicate sampling as well as sampled queries
					 * Solution is to use the genreId from earlier, if it's -1 that means the track sampled another
					 */
					String sampleQuery = (genreId == -1) ? updateSamplingIdQuery : updateSampledIdQuery;
					prepStat = conn.prepareStatement(sampleQuery);
					prepStat.setInt(1, minTrackId);
					prepStat.setInt(2, dupId);
					prepStat.executeUpdate();
					// Now delete this track record
					prepStat = conn.prepareStatement(deleteTrackIdQuery);
					prepStat.setInt(1, dupId);
					int colsDeleted = prepStat.executeUpdate();
					assert(colsDeleted == 1);
				}
				dupIdSet.close();
			}
			trackResults.close();	
			conn.commit();	
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done removing duplicate track titles");
		System.out.println("Done parsing records into DB");
		/*
		 * Begin writing out json files for d3 to use:
		 * 1) A list of artists that are sampled
		 * 2) A list of years and number of times an artist was sampled, indexed by artist ID
		 */
		// List of sampled artists
		List<SampledArtist> sas = new ArrayList<SampledArtist>();
		// List of years and # of times an artist was sampled
		Map<Integer,List<SampledArtistYearCount>> artistYearMap = new HashMap<Integer,List<SampledArtistYearCount>>();
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			Statement stat = conn.createStatement();
			String sampledArtistsQuery = "SELECT DISTINCT artists.id, artists.name AS 'name', COUNT(samples.sampled_id) as 'count' FROM artists INNER JOIN tracks ON artists.id = tracks.artist_id INNER JOIN samples ON tracks.id = samples.sampled_id GROUP BY artists.name ORDER BY COUNT(samples.sampled_id) ASC;";
			String yearsArtistSampledQuery = "SELECT tracks.year, COUNT(tracks.year) AS count FROM tracks INNER JOIN samples ON tracks.id = samples.sampler_id WHERE samples.sampled_id IN (SELECT tracks.id FROM tracks WHERE artist_id=?) GROUP BY tracks.year ORDER BY tracks.year ASC;";
			// Get list of sampled artists
			ResultSet sampledArtSet = stat.executeQuery(sampledArtistsQuery);
			int index = 1;
			while (sampledArtSet.next()) {
				int artistId = sampledArtSet.getInt("id");
				String name = sampledArtSet.getString("name");
				int sampleCount = sampledArtSet.getInt("count");
				// Get list of years and # times artist was sampled
				PreparedStatement ps = conn.prepareStatement(yearsArtistSampledQuery);
				ps.setInt(1, artistId);
				ResultSet yearSet = ps.executeQuery();
				List<SampledArtistYearCount> yearCounts = new ArrayList<SampledArtistYearCount>();
				while (yearSet.next()) {
					int year = yearSet.getInt("year");
					int count = yearSet.getInt("count");
					SampledArtistYearCount yearCount = new SampledArtistYearCount(year, count);
					yearCounts.add(yearCount);
				}
				yearSet.close();
				artistYearMap.put(artistId, yearCounts);
				SampledArtist sa = new SampledArtist(artistId, name, sampleCount, index++);
			 	sas.add(sa);
			 	System.out.println("Done with an artist and his/her samples");
			}
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		/*
		 * Need to output a map with name = artists, children = all those kids
		 */
		Map<String, List<SampledArtist>> sampledArtists = new HashMap<String, List<SampledArtist>>();
		sampledArtists.put("children", sas);
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File(SAMPLED_ARTISTS_JSON_PATH), sampledArtists);
			mapper.writeValue(new File(YEARS_ARTISTS_SAMPLED_PATH), artistYearMap);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done writing json files");
	}
}
