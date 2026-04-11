package com.newsrec.manage.mapper;

import java.util.List;
import com.newsrec.manage.domain.Messages;
import org.apache.ibatis.annotations.Select;

/**
 * 留言Mapper接口
 *
 * @author weiyan
 * @date 2025-03-17
 */
public interface MessagesMapper
{

    /**
     * 多级留言树状结构
     * @return 留言板
     */

    //获取顶级留言列表，实现多级列表
    public List<Messages> selectTopMessages();

    //查询某个顶级留言下的所有子留言。
    public List<Messages> selectChildMessages(Long parentId);

    //获取留言总条数
    @Select("SELECT COUNT(*) FROM tb_messages")
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
     * 删除留言
     *
     * @param messageId 留言主键
     * @return 结果
     */
    public int deleteMessagesByMessageId(Long messageId);

    /**
     * 批量删除留言
     *
     * @param messageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMessagesByMessageIds(Long[] messageIds);
}
