/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.database.jooq.tables.records;


import de.kittybot.kittybot.database.jooq.tables.UserStatistics;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UserStatisticsRecord extends UpdatableRecordImpl<UserStatisticsRecord> implements Record9<String, String, Integer, Integer, Integer, Integer, Integer, Integer, String> {

    private static final long serialVersionUID = -2049046050;

    /**
     * Setter for <code>public.user_statistics.user_id</code>.
     */
    public void setUserId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.user_statistics.user_id</code>.
     */
    public String getUserId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.user_statistics.guild_id</code>.
     */
    public void setGuildId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.user_statistics.guild_id</code>.
     */
    public String getGuildId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.user_statistics.xp</code>.
     */
    public void setXp(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.user_statistics.xp</code>.
     */
    public Integer getXp() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>public.user_statistics.level</code>.
     */
    public void setLevel(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.user_statistics.level</code>.
     */
    public Integer getLevel() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>public.user_statistics.bot_calls</code>.
     */
    public void setBotCalls(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.user_statistics.bot_calls</code>.
     */
    public Integer getBotCalls() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>public.user_statistics.voice_time</code>.
     */
    public void setVoiceTime(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.user_statistics.voice_time</code>.
     */
    public Integer getVoiceTime() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>public.user_statistics.message_count</code>.
     */
    public void setMessageCount(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.user_statistics.message_count</code>.
     */
    public Integer getMessageCount() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>public.user_statistics.emote_count</code>.
     */
    public void setEmoteCount(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.user_statistics.emote_count</code>.
     */
    public Integer getEmoteCount() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>public.user_statistics.last_active</code>.
     */
    public void setLastActive(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.user_statistics.last_active</code>.
     */
    public String getLastActive() {
        return (String) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<String, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<String, String, Integer, Integer, Integer, Integer, Integer, Integer, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<String, String, Integer, Integer, Integer, Integer, Integer, Integer, String> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return UserStatistics.USER_STATISTICS.USER_ID;
    }

    @Override
    public Field<String> field2() {
        return UserStatistics.USER_STATISTICS.GUILD_ID;
    }

    @Override
    public Field<Integer> field3() {
        return UserStatistics.USER_STATISTICS.XP;
    }

    @Override
    public Field<Integer> field4() {
        return UserStatistics.USER_STATISTICS.LEVEL;
    }

    @Override
    public Field<Integer> field5() {
        return UserStatistics.USER_STATISTICS.BOT_CALLS;
    }

    @Override
    public Field<Integer> field6() {
        return UserStatistics.USER_STATISTICS.VOICE_TIME;
    }

    @Override
    public Field<Integer> field7() {
        return UserStatistics.USER_STATISTICS.MESSAGE_COUNT;
    }

    @Override
    public Field<Integer> field8() {
        return UserStatistics.USER_STATISTICS.EMOTE_COUNT;
    }

    @Override
    public Field<String> field9() {
        return UserStatistics.USER_STATISTICS.LAST_ACTIVE;
    }

    @Override
    public String component1() {
        return getUserId();
    }

    @Override
    public String component2() {
        return getGuildId();
    }

    @Override
    public Integer component3() {
        return getXp();
    }

    @Override
    public Integer component4() {
        return getLevel();
    }

    @Override
    public Integer component5() {
        return getBotCalls();
    }

    @Override
    public Integer component6() {
        return getVoiceTime();
    }

    @Override
    public Integer component7() {
        return getMessageCount();
    }

    @Override
    public Integer component8() {
        return getEmoteCount();
    }

    @Override
    public String component9() {
        return getLastActive();
    }

    @Override
    public String value1() {
        return getUserId();
    }

    @Override
    public String value2() {
        return getGuildId();
    }

    @Override
    public Integer value3() {
        return getXp();
    }

    @Override
    public Integer value4() {
        return getLevel();
    }

    @Override
    public Integer value5() {
        return getBotCalls();
    }

    @Override
    public Integer value6() {
        return getVoiceTime();
    }

    @Override
    public Integer value7() {
        return getMessageCount();
    }

    @Override
    public Integer value8() {
        return getEmoteCount();
    }

    @Override
    public String value9() {
        return getLastActive();
    }

    @Override
    public UserStatisticsRecord value1(String value) {
        setUserId(value);
        return this;
    }

    @Override
    public UserStatisticsRecord value2(String value) {
        setGuildId(value);
        return this;
    }

    @Override
    public UserStatisticsRecord value3(Integer value) {
        setXp(value);
        return this;
    }

    @Override
    public UserStatisticsRecord value4(Integer value) {
        setLevel(value);
        return this;
    }

    @Override
    public UserStatisticsRecord value5(Integer value) {
        setBotCalls(value);
        return this;
    }

    @Override
    public UserStatisticsRecord value6(Integer value) {
        setVoiceTime(value);
        return this;
    }

    @Override
    public UserStatisticsRecord value7(Integer value) {
        setMessageCount(value);
        return this;
    }

    @Override
    public UserStatisticsRecord value8(Integer value) {
        setEmoteCount(value);
        return this;
    }

    @Override
    public UserStatisticsRecord value9(String value) {
        setLastActive(value);
        return this;
    }

    @Override
    public UserStatisticsRecord values(String value1, String value2, Integer value3, Integer value4, Integer value5, Integer value6, Integer value7, Integer value8, String value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserStatisticsRecord
     */
    public UserStatisticsRecord() {
        super(UserStatistics.USER_STATISTICS);
    }

    /**
     * Create a detached, initialised UserStatisticsRecord
     */
    public UserStatisticsRecord(String userId, String guildId, Integer xp, Integer level, Integer botCalls, Integer voiceTime, Integer messageCount, Integer emoteCount, String lastActive) {
        super(UserStatistics.USER_STATISTICS);

        set(0, userId);
        set(1, guildId);
        set(2, xp);
        set(3, level);
        set(4, botCalls);
        set(5, voiceTime);
        set(6, messageCount);
        set(7, emoteCount);
        set(8, lastActive);
    }
}
