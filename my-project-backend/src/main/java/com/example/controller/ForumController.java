package com.example.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.RestBean;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.*;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import com.example.utils.Const;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RequestMapping("/api/forum")


@RestController
public class ForumController {
    @Resource
    WeatherService service;

    @Resource
    TopicService topicService;

    @Resource
    WeatherService weatherService;


    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude,double latitude){
        WeatherVO vo=weatherService.fetchWeather(longitude,latitude);
        return vo==null?

                RestBean.failure(400,"获取地理信息与天气失败，请联系管理员"):RestBean.success(vo);
    }
    @Resource
    ControllerUtils utils;
    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>>listTypes(){
        return RestBean.success(topicService.listTypes().stream().map(type->type.asViewObject(TopicTypeVO.class)).toList());
    }
    @PostMapping("/create-topic")
    public RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVO vo,
                                      @RequestAttribute (Const.ATTR_USER_ID) int id){
        return utils.messageHandle(()->topicService.createTopic(id,vo));
    }
    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVO>>listTopic(@RequestParam @Min(0) int page,@RequestParam @Min(0) int type){
        return RestBean.success(topicService.listTopicByPage(1+page,type));
    }
    @GetMapping("/top-topic")
    public RestBean<List<TopicTopVO>>topTopic(){
        return RestBean.success(topicService.listTopTopic());
    }
    @GetMapping("/topic")
    public RestBean<TopicDetailVO>topic(@RequestParam @Min(0) int tid,@RequestAttribute (Const.ATTR_USER_ID)int id){
        return RestBean.success(topicService.getTopic(tid,id));
    }
    @GetMapping("/interact")
    public RestBean<Void>interact(@RequestParam @Min(0) int tid,
                                  @RequestParam @Pattern(regexp = "(like|collect)")String type,
                                  @RequestParam boolean state,
                                  @RequestAttribute (Const.ATTR_USER_ID)int id){
        topicService.interact(new Interact(tid,id,new Date(),type),state);
        return RestBean.success();

    }
    @GetMapping("/collects")
    public RestBean<List<TopicPreviewVO>>collects(@RequestAttribute (Const.ATTR_USER_ID)int id){
        return RestBean.success(topicService.listTopicCollects(id));
    }

    @PostMapping("/update-topic")
    public RestBean<Void>updateTopic(@Valid @RequestBody TopicUpdateVO vo,@RequestAttribute(Const.ATTR_USER_ID)int id){










        return utils.messageHandle(()->topicService.updateTopic(id,vo));
    }
    @PostMapping("/add-comment")
    public RestBean<Void>addComment(@Valid @RequestBody AddCommentVO vo,@RequestAttribute(Const.ATTR_USER_ID)int id){
        return utils.messageHandle(()->topicService.createComment(id,vo));
    }
    @GetMapping("/comments")
    public RestBean<List<CommentVO>>comments(@RequestParam @Min(0) int tid,
                                             @RequestParam @Min(0) int page){
        return  RestBean.success(topicService.comments(tid,1+page));
    }
    @GetMapping("/delete-comment")
    public RestBean<Void>deleteComment(@RequestParam @Min(0)int id, @RequestAttribute(Const.ATTR_USER_ID)int uid){
        topicService.deleteComment(id,uid);
        return RestBean.success() ;
    }
}
