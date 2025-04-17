package com.ujiKom.ukkKasir.Members;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MembersServiceImpl implements MembersService{
    private final MembersRepository repository;

    public MembersServiceImpl(MembersRepository repository) {
        this.repository = repository;
    }

    @Override
    public Members addData(Members member, HttpServletRequest request){
        Optional<Members> existingMember = repository.findByPhoneNumber(member.getPhoneNumber());
        if (existingMember.isPresent()) {
            return existingMember.get();
        }

        Members newMember = new Members();
        newMember.setPhoneNumber(member.getPhoneNumber());
        newMember.setName(member.getName());
        return repository.save(newMember);
    }
}
