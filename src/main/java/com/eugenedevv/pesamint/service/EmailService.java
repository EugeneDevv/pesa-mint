package com.eugenedevv.pesamint.service;

import com.eugenedevv.pesamint.dto.EmailDetails;

/**
 * Created by Eugene Devv on 22 Oct, 2023
 */
public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);
}
