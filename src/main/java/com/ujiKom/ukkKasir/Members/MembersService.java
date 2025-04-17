package com.ujiKom.ukkKasir.Members;

import jakarta.servlet.http.HttpServletRequest;

public interface MembersService {
    Members addData(Members member, HttpServletRequest request);
}
