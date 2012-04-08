package com.webmdee.samplevis.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.webmdee.samplevis.model.D3NameChildrenWrapper;
import com.webmdee.samplevis.model.SampledTrack;
import com.webmdee.samplevis.model.SamplingTrack;


/**
 * Iterates over the sampled artists, finds all of the information
 * about each sampled track for that sampled artist, and puts it into a hierarchical form
 * The output of this will support the sunburst and dendrogram visualizations.
 * @author MDee
 *
 */
public class SampleTreeParser {

	public static void main(String[] args) {
		try {
			// The path to the SQLite DB file that will be created and read from
			final String DB_PATH ="/Users/MDee/Desktop/data.db";
			// The path to a directory which will contain each artist's sampling track JSON file
			final String ARTIST_SAMPLED_TRACK_DIR_PATH = "/Users/MDee/Desktop/Viz/";
			Class.forName("org.sqlite.JDBC");
			Connection conn =
					DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			String sampledTracksQuery = "SELECT DISTINCT tracks.id, tracks.title, tracks.year, genres.id as genre_id, genres.name as genre_name, albums.id as album_id, albums.title as album_title FROM tracks INNER JOIN albums ON albums.id = tracks.album_id INNER JOIN samples ON samples.sampled_id = tracks.id INNER JOIN genres ON genres.id = tracks.genre_id WHERE tracks.artist_id=? ORDER BY tracks.title ASC;";
			String samplingTrackSampleInfoQuery = "SELECT samples.sampler_id, samples.sampled_id, samples.sample_type, samples.part_sampled, samples.who_sampled_url, tracks.id, tracks.title, tracks.year, genres.id as genre_id, genres.name as genre_name, albums.id as album_id, albums.title as album_title, artists.id as artist_id, artists.name as artist_name FROM samples INNER JOIN tracks on samples.sampler_id = tracks.id INNER JOIN albums ON albums.id = tracks.album_id INNER JOIN artists on artists.id = tracks.artist_id INNER JOIN genres ON genres.id = tracks.genre_id WHERE sampled_id = ? ORDER BY samples.part_sampled ASC;";
			// These are the IDs of all sampled artists
			int[] sampledArtists = {861, 66, 297, 128, 161, 1, 1742, 261, 206, 2540, 702, 2390, 95, 693, 2890, 626, 3362, 3058, 82, 619};
			
			PreparedStatement ps;
			for (int i=0; i < sampledArtists.length; i++) {
				int artistId = sampledArtists[i];
				ps = conn.prepareStatement(sampledTracksQuery);
				ps.setInt(1, artistId);
				ResultSet sampledTrackSet = ps.executeQuery();
				D3NameChildrenWrapper artistSampledTracks;
				List<D3NameChildrenWrapper> sampledTracks = new ArrayList<D3NameChildrenWrapper>();
				while (sampledTrackSet.next()) {
					
					D3NameChildrenWrapper sampledTrackSamplingTracks;
					int sampledTrackId = sampledTrackSet.getInt("id");
					String sampledTrackTitle = sampledTrackSet.getString("title");
					int sampledYear = sampledTrackSet.getInt("year");
					int genreId = sampledTrackSet.getInt("genre_id");
					String genreName = sampledTrackSet.getString("genre_name");
					int albumId = sampledTrackSet.getInt("album_id");
					String albumTitle = sampledTrackSet.getString("album_title");
					SampledTrack sampledTrack = new SampledTrack(sampledTrackId, sampledTrackTitle, sampledYear, genreId, genreName, albumId, albumTitle);
					// Get list of sampling tracks
					List<SampledTrack> samplingTracks = new ArrayList<SampledTrack>();
					ps = conn.prepareStatement(samplingTrackSampleInfoQuery);
					ps.setInt(1, sampledTrackId);
					ResultSet samplingTrackSet = ps.executeQuery();
					
					while (samplingTrackSet.next()) {
						int samplingTrackId = samplingTrackSet.getInt("sampler_id");
						Character sampleType = new Character(samplingTrackSet.getString("sample_type").toCharArray()[0]);
						Character partSampled = new Character(samplingTrackSet.getString("part_sampled").toCharArray()[0]);
						String url = samplingTrackSet.getString("who_sampled_url");
						String samplingTrackTitle = samplingTrackSet.getString("title");
						int samplingYear = samplingTrackSet.getInt("year");
						int samplingGenreId = samplingTrackSet.getInt("genre_id");
						String samplingGenreName = samplingTrackSet.getString("genre_name");
						int samplingArtistId = samplingTrackSet.getInt("artist_id");
						String samplingArtistName = samplingTrackSet.getString("artist_name");
						int samplingAlbumId = samplingTrackSet.getInt("album_id");
						String samplingAlbumTitle = samplingTrackSet.getString("album_title");
						SamplingTrack samplingTrack = new SamplingTrack(samplingTrackId, samplingTrackTitle, samplingYear, samplingGenreId, samplingGenreName, samplingAlbumId, samplingAlbumTitle, sampleType, partSampled, url, samplingArtistId, samplingArtistName);
						samplingTracks.add(samplingTrack);
						System.out.println("Done with a sampling track");
					}
					samplingTrackSet.close();
					sampledTrackSamplingTracks = new D3NameChildrenWrapper(sampledTrack.toString(), samplingTracks);
					sampledTracks.add(sampledTrackSamplingTracks);
					System.out.println("Done with a sampled track");
				}
				// hacky...
				artistSampledTracks = new D3NameChildrenWrapper(artistId + "", sampledTracks);
				ObjectMapper mapper = new ObjectMapper();
				try {
					boolean dirMade = new File(ARTIST_SAMPLED_TRACK_DIR_PATH+artistId+"/").mkdirs();
					assert(dirMade);
					mapper.writeValue(new File(ARTIST_SAMPLED_TRACK_DIR_PATH + artistId + "/samplingTracks.json"), artistSampledTracks);
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
				sampledTrackSet.close();
				System.out.println("Done with an artist and his/her samples");
			}
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
