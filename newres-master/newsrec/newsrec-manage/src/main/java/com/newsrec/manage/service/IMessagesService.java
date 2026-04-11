package com.newsrec.manage.service;

import java.util.List;
import com.newsrec.manage.domain.Messages;

/**
 * 留言Service接口
 *
 * @author weiyan
 * @date 2025-03-17
 */
public interface IMessagesService
{

    public List<Messages> buildMessageTree(List<Messages> topLevelMessages);

    public List<Messages> getAllMessages();


   //获取当前留言数量
    public Integer countMessage();


    /**
     * 查询留言
     *
     * @param messageId 留言主键
     * @return 留言
     */
    public Messages selectMessagesByMessageId(Long messageId);

    /**
     * 查询留言列表
     *
     * @param messages 留言
     * @return 留言集合
     */
    public List<Messages> selectMessagesList(Messages messages);

    /**
     * 新增留言
     *
     * @param messages 留言
     * @return 结果
     */
    public int insertMessages(Messages messages);

    /**
     * 修改留言
     *
     * @param messages 留言
     * @return 结果
     */
    public int updateMessages(Messages messages);

    /**
     * 批量删除留言
     *
     * @param messageIds 需要删除的留言主键集合
     * @return 结果
     */
    public int deleteMessagesByMessageIds(Long[] messageIds);

    /**
     * 删除留言信息
     *
     * @param messageId 留言主键
     * @return 结果
     */
    public int deleteMessagesByMessageId(Long messageId);
}
