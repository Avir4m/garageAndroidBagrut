package com.example.garage.models;

import java.util.List;

public class Event {

    private String id;
    private String date;
    private String host;
    private String hostId;
    private String location;
    private List<String> participants;
    private int participantsCount;
    private String time;
    private String title;

    public Event() {
    }

    public Event(String id, String date, String host, String hostId, String location, List<String> participants, int participantsCount, String time, String title) {
        this.id = id;
        this.date = date;
        this.host = host;
        this.hostId = hostId;
        this.location = location;
        this.participants = participants;
        this.participantsCount = participantsCount;
        this.time = time;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
