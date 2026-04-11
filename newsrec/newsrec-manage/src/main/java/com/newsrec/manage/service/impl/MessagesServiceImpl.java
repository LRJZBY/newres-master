package com.newsrec.manage.service.impl;

import java.util.List;
import com.newsrec.common.utils.DateUtils;
import org.springframework.stereotype.Service;
import com.newsrec.manage.mapper.MessagesMapper;
import com.newsrec.manage.domain.Messages;
import com.newsrec.manage.service.IMessagesService;

import javax.annotation.Resource;

/**
 * 留言Service业务层处理
 *
 * @author weiyan
 * @date 2025-03-17
 */
@Service
public class MessagesServiceImpl implements IMessagesService
{
    @Resource
    private MessagesMapper messagesMapper;

    //获取顶级留言板
    @Override
    public List<Messages> getAllMessages() {
        List<Messages> topLevelMessages = messagesMapper.selectTopMessages();
        return buildMessageTree(topLevelMessages);
    }

    //递归构建留言树结构
    @Override
    public List<Messages> buildMessageTree(List<Messages> topLevelMessages) {
        //查询当前父留言列表
        for (Messages message : topLevelMessages) {
            //获取其中子留言
            List<Messages> childMessages = messagesMapper.selectChildMessages(message.getMessageId());
            message.setChildren(childMessages);
            buildMessageTree(childMessages); // 递归构建子留言
        }
        return topLevelMessages;
    }

    @Override
   public Integer countMessage() {
        return messagesMapper.countMessage();
    }

    /**
     * 查询留言
     *
     * @param messageId 留言主键
     * @return 留言
     */
    @Override
    public Messages selectMessagesByMessageId(Long messageId)
    {
        return messagesMapper.selectMessagesByMessageId(messageId);
    }

    /**
     * 查询留言列表
     *
     * @param messages 留言
     * @return 留言
     */
    @Override
    public List<Messages> selectMessagesList(Messages messages)
    {
        return messagesMapper.selectMessagesList(messages);
    }

    /**
     * 新增留言
     *
     * @param messages 留言
     * @return 结果
     */
    @Override
    public int insertMessages(Messages messages)
    {
        messages.setCreateTime(DateUtils.getNowDate());
        return messagesMapper.insertMessages(messages);
    }

    /**
     * 修改留言
     *
     * @param messages 留言
     * @return 结果
     */
    @Override
    public int updateMessages(Messages messages)
    {
        messages.setUpdateTime(DateUtils.getNowDate());
        return messagesMapper.updateMessages(messages);
    }

    /**
     * 批量删除留言
     *
     * @param messageIds 需要删除的留言主键
     * @return 结果
     */
    @Override
    public int deleteMessagesByMessageIds(Long[] messageIds)
    {
        return messagesMapper.deleteMessagesByMessageIds(messageIds);
    }

    /**
     * 删除留言信息
     *
     * @param messageId 留言主键
     * @return 结果
     */
    @Override
    public int deleteMessagesByMessageId(Long messageId)
    {
        return messagesMapper.deleteMessagesByMessageId(messageId);
    }
}
