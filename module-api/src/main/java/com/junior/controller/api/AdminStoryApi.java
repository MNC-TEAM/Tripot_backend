package com.junior.controller.api;

import com.junior.dto.story.AdminStoryDetailDto;
import com.junior.dto.story.AdminStoryDto;
import com.junior.page.PageCustom;
import com.junior.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Story Admin")
public interface AdminStoryApi {


    @Operation(summary = "스토리 관리자 조회", description = "스토리를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "스토리 불러오기 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "STORY-SUCCESS-0001",
                                                        "customMessage": "스토리 불러오기 성공",
                                                        "status": true,
                                                        "data": {
                                                          "content": [
                                                            {
                                                              "title": "testStoryTitle",
                                                              "city": "city",
                                                              "id": 3,
                                                              "createdUsername": "테스트사용자유저네임",
                                                              "isDeleted": false
                                                            },
                                                            {
                                                              "title": "testStoryTitle",
                                                              "city": "city",
                                                              "id": 2,
                                                              "createdUsername": "테스트사용자유저네임",
                                                              "isDeleted": false
                                                            },
                                                            {
                                                              "title": "testStoryTitle",
                                                              "city": "city",
                                                              "id": 1,
                                                              "createdUsername": "테스트사용자유저네임",
                                                              "isDeleted": false
                                                            }
                                                          ],
                                                          "pageable": {
                                                            "number": 2,
                                                            "size": 15,
                                                            "sort": {
                                                              "empty": true,
                                                              "unsorted": true,
                                                              "sorted": false
                                                            },
                                                            "first": false,
                                                            "last": true,
                                                            "hasNext": false,
                                                            "totalPages": 2,
                                                            "totalElements": 18,
                                                            "numberOfElements": 3,
                                                            "empty": false
                                                          }
                                                        }
                                                      }
                                                    """
                                    )))
            })
    public CommonResponse<PageCustom<AdminStoryDto>> findStory(@PageableDefault(size = 15, page = 1) Pageable pageable, @RequestParam(required = false, name = "q", defaultValue = "") String q);


    @Operation(summary = "스토리 상세조회", description = "관리자 페이지에서 스토리를 상세조회 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "스토리 상세조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    		"customCode":"STORY-SUCCESS-0001",
                                                    		"customMessage":"스토리 불러오기 성공",
                                                    		"status":true,
                                                    		"data":{
                                                    				"id":10,
                                                    				"title":"testStoryTitle",
                                                    				"content":"testStoryContent",
                                                    				"thumbnailImg":"thumbURL",
                                                    				"latitude":1.0,
                                                    				"longitude":1.0,
                                                    				"city":"city",
                                                    				"likeCnt":0,
                                                    				"createDate":"2025-01-08T23:47:31.364389",
                                                    				"imgUrls":[
                                                    						"imgUrl1",
                                                    						"imgUrl2",
                                                    						"imgUrl3"
                                                    				],
                                                    				"isDeleted":false
                                                    		}
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "스토리 찾기 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "유효하지 않은 회원",
                                                    value = """
                                                            {
                                                                "customCode": "STORY-ERR-0002",
                                                                "customMessage": "스토리 불러오기 실패",
                                                                "status": false,
                                                                "data": null
                                                              }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<AdminStoryDetailDto> findStoryDetail(@PathVariable(name = "story_id") Long storyId);

    @Operation(summary = "관리자용 스토리 삭제", description = "관리자 페이지에서 스토리를 삭제 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "스토리 삭제 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "STORY-SUCCESS-0002",
                                                        "customMessage": "스토리 삭제 성공",
                                                        "status": true,
                                                        "data": null
                                                      }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "스토리 찾기 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "스토리 불러오기 실패",
                                                    value = """
                                                            {
                                                                "customCode": "STORY-ERR-0002",
                                                                "customMessage": "스토리 불러오기 실패",
                                                                "status": false,
                                                                "data": null
                                                              }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<Object> deleteStory(@PathVariable(name = "story_id") Long storyId);
}
