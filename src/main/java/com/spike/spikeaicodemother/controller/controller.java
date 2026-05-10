package com.spike.spikeaicodemother.controller;

import com.spike.spikeaicodemother.common.BaseResponse;
import com.spike.spikeaicodemother.utils.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class controller {

    @GetMapping("/")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
