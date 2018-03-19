package com.thong.youtube.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.thong.youtube.entity.PlayListInfoEntity;
import com.thong.youtube.entity.ResponseEntity;
import com.thong.youtube.entity.VideoInfo;
import com.thong.youtube.service.PlaylistUpdatesService;
import com.thong.youtube.service.SearchService;
import com.thong.youtube.utils.Constants;

@CrossOrigin
@RestController
public class PlaylistUpdatesController {
	
	private static final Logger logger = LogManager.getLogger(PlaylistUpdatesController.class);


    @Autowired
    private PlaylistUpdatesService playlistUpdatesService;
    
    @Autowired
    private SearchService searchService;
    
    @RequestMapping("*/")
    public String welcome() {
    	return "Welcome to Spring Boot Tutorials";
    }

    @RequestMapping(method=RequestMethod.POST, value="/addPlaylist")
    public ResponseEntity<PlayListInfoEntity> greeting(@RequestBody PlayListInfoEntity playlistInfo) {
    	logger.info("playlistInfo input: " + playlistInfo);
    	ResponseEntity<PlayListInfoEntity> addingPlaylistResult = playlistUpdatesService.addPlayList(playlistInfo.getName());
    	if(addingPlaylistResult.getCode() == Constants.ERROR_CODE) {
    		return addingPlaylistResult;
    	} else {
    		// Insert and get playlist id
    		PlayListInfoEntity playListInfoEntity = addingPlaylistResult.getData();
    		String playlistId = playListInfoEntity.getId();
    		
    		// get list of videos by playlistName
    		ResponseEntity<List<VideoInfo>> videoInfosResponse = searchService.searchVideo(playlistInfo.getName());
    		System.out.println("OK: " + videoInfosResponse.getCode());
    		
    		
    		List<VideoInfo> VideoInfoEntities = videoInfosResponse.getData();
    		
    		System.out.println("OK: " + VideoInfoEntities.size());
    		// Insert videos into playlist
    		ResponseEntity<PlayListInfoEntity> addingVideoResult = new ResponseEntity<PlayListInfoEntity>(Constants.ERROR_CODE, "Can't insert video");
    		for(VideoInfo video : VideoInfoEntities) {
    			addingVideoResult = playlistUpdatesService.insertPlaylistItem(playlistId, video.getId());
    			if(addingVideoResult.getCode() == Constants.ERROR_CODE) {
    				return addingVideoResult;
    			}
    		}
    		
    		return addingPlaylistResult;
    	}
    }
}