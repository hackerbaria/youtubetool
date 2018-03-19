/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thong.youtube.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.samples.youtube.cmdline.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.PlaylistStatus;
import com.google.api.services.youtube.model.ResourceId;
import com.google.common.collect.Lists;
import com.thong.youtube.entity.PlayListInfoEntity;
import com.thong.youtube.entity.ResponseEntity;
import com.thong.youtube.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Creates a new, private playlist in the authorized user's channel and add
 * a video to that new playlist.
 *
 * @author Thong
 */
@Service
public class PlaylistUpdatesService {

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;


    private static final Logger logger = LogManager.getLogger(PlaylistUpdatesService.class);
    
    /**
     * Authorize the user, create a playlist, and add an item to the playlist.
     *
     * @param args command line args (not used).
     */
    public ResponseEntity<PlayListInfoEntity> addPlayList(String playListName) {
    	logger.info("start function addPlayList service " + playListName);

        // This OAuth 2.0 access scope allows for full read/write access to the
        // authenticated user's account.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
        logger.info("scopes: " + scopes);
        try {
            // Authorize the request.
        	logger.info("before call : authorize");
            Credential credential = Auth.authorize(scopes, "playlistupdates");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube-cmdline-playlistupdates-sample")
                    .build();

            // Create a new, private playlist in the authorized user's channel.
            String description = "A private playlist created with the YouTube API v3";
            PlayListInfoEntity playListInfoEntity = insertPlaylist(playListName, description);
            
            return new ResponseEntity<PlayListInfoEntity>(0, Constants.SUCCESS, playListInfoEntity );
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
            return new ResponseEntity<PlayListInfoEntity>(-1, "There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return new ResponseEntity<PlayListInfoEntity>(-1, "IOException: " + e.getMessage());
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            return new ResponseEntity<PlayListInfoEntity>(-1, "Throwable: " + t.getMessage());
        }
    }

    /**
     * Create a playlist and add it to the authorized account.
     */
    private static PlayListInfoEntity insertPlaylist(String title, String description) throws IOException {

        // This code constructs the playlist resource that is being inserted.
        // It defines the playlist's title, description, and privacy status.
        PlaylistSnippet playlistSnippet = new PlaylistSnippet();
        playlistSnippet.setTitle(title);
        playlistSnippet.setDescription(description);
        PlaylistStatus playlistStatus = new PlaylistStatus();
        playlistStatus.setPrivacyStatus("public");

        Playlist youTubePlaylist = new Playlist();
        youTubePlaylist.setSnippet(playlistSnippet);
        youTubePlaylist.setStatus(playlistStatus);

        // Call the API to insert the new playlist. In the API call, the first
        // argument identifies the resource parts that the API response should
        // contain, and the second argument is the playlist being inserted.
        YouTube.Playlists.Insert playlistInsertCommand =
                youtube.playlists().insert("snippet,status", youTubePlaylist);
        Playlist playlistInfo = playlistInsertCommand.execute();

        return new PlayListInfoEntity(playlistInfo.getId(), 
        							  playlistInfo.getSnippet().getTitle(), 
        							  playlistInfo.getStatus().getPrivacyStatus(),
        							  playlistInfo.getSnippet().getDescription(),
        							  playlistInfo.getSnippet().getPublishedAt(),
        							  playlistInfo.getSnippet().getChannelId());

    }

    /**
     * Create a playlist item with the specified video ID and add it to the
     * specified playlist.
     *
     * @param playlistId assign to newly created playlistitem
     * @param videoId    YouTube video id to add to playlistitem
     */
    public ResponseEntity<PlayListInfoEntity> insertPlaylistItem(String playlistId, String videoId){

        // Define a resourceId that identifies the video being added to the
        // playlist.
        ResourceId resourceId = new ResourceId();
        resourceId.setKind("youtube#video");
        resourceId.setVideoId(videoId);

        // Set fields included in the playlistItem resource's "snippet" part.
        PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
        playlistItemSnippet.setTitle("First video in the test playlist");
        playlistItemSnippet.setPlaylistId(playlistId);
        playlistItemSnippet.setResourceId(resourceId);

        // Create the playlistItem resource and set its snippet to the
        // object created above.
        PlaylistItem playlistItem = new PlaylistItem();
        playlistItem.setSnippet(playlistItemSnippet);

        // Call the API to add the playlist item to the specified playlist.
        // In the API call, the first argument identifies the resource parts
        // that the API response should contain, and the second argument is
        // the playlist item being inserted.
        YouTube.PlaylistItems.Insert playlistItemsInsertCommand;
		try {
			playlistItemsInsertCommand = youtube.playlistItems().insert("snippet,contentDetails", playlistItem);
			PlaylistItem returnedPlaylistItem = playlistItemsInsertCommand.execute();
			PlayListInfoEntity playListInfoEntity = new PlayListInfoEntity(returnedPlaylistItem.getId(), 
																    		returnedPlaylistItem.getSnippet().getTitle(), 
																    		null,
																    		returnedPlaylistItem.getSnippet().getDescription(),
																    		returnedPlaylistItem.getSnippet().getPublishedAt(),
																    		returnedPlaylistItem.getSnippet().getChannelId());
			
			return new ResponseEntity<PlayListInfoEntity>(Constants.SUCCESS_CODE, Constants.SUCCESS, playListInfoEntity);
		} catch (IOException e) {
			return new ResponseEntity<PlayListInfoEntity>(Constants.ERROR_CODE, e.getMessage());
		}
        

        // Print data from the API response and return the new playlist
        // item's unique playlistItem ID.

//        System.out.println("New PlaylistItem name: " + returnedPlaylistItem.getSnippet().getTitle());
//        System.out.println(" - Video id: " + returnedPlaylistItem.getSnippet().getResourceId().getVideoId());
//        System.out.println(" - Posted: " + returnedPlaylistItem.getSnippet().getPublishedAt());
//        System.out.println(" - Channel: " + returnedPlaylistItem.getSnippet().getChannelId());
//        return returnedPlaylistItem.getId();
        
        

    }
}
