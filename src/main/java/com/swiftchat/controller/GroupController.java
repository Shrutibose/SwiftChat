package com.swiftchat.controller;

import com.swiftchat.dto.GroupMessage;
import com.swiftchat.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // CREATE GROUP
    @PostMapping("/create")
    public String createGroup(@RequestParam String groupName,
                              @RequestParam String createdBy) {
        return groupService.createGroup(groupName, createdBy);
    }

    // ADD MEMBER
    @PostMapping("/add-member")
    public String addMember(@RequestParam String groupName,
                            @RequestParam String username) {
        return groupService.addMember(groupName, username);
    }

    // GET MEMBERS
    @GetMapping("/members")
    public Set<String> getMembers(@RequestParam String groupName) {
        return groupService.getMembers(groupName);
    }

    // SEND MESSAGE
    @PostMapping("/send")
    public String sendMessage(@RequestParam String groupName,
                              @RequestParam String from,
                              @RequestParam String message) {
        return groupService.sendGroupMessage(groupName, from, message);
    }

    // GET MESSAGES
    @GetMapping("/messages")
    public List<GroupMessage> getMessages(@RequestParam String groupName) {
            return groupService.getGroupMessages(groupName);
        }
    }