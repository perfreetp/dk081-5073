package com.safetycampus.supervise.service;

import com.safetycampus.supervise.dto.AlarmTransferDTO;
import com.safetycampus.supervise.dto.HandleFeedbackDTO;
import com.safetycampus.supervise.dto.SuperviseCreateDTO;

public interface SuperviseService {

    boolean createSupervise(SuperviseCreateDTO dto);

    boolean handleFeedback(HandleFeedbackDTO dto);

    boolean transferAlarm(AlarmTransferDTO dto);

    boolean closeAlarm(Long id, String remark);

    boolean manualRemind(Long id);

    void remindTimeout();
}
