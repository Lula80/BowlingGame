package yuliya.akkuzhyna.dto;

import java.util.List;

public record BoardDto (int score,
    List<FrameDto> frameDtos
, String user){}
