package com.resumejdbc.repository;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.resumejdbc.entity.MemberResume;
import com.resumejdbc.entity.Resume;

@Repository
public class ResumeJdbcRepository {

	private NamedParameterJdbcTemplate template;

	/**
	 * トラクタ
	 * @param template NamedParameterJdbcTemplate
	 */
	public ResumeJdbcRepository(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	/**
	 * 会員IDを検索条件にresumesテーブルを検索する
	 * @param id ID
	 * @return
	 * @throws Exception
	 */
	public List<Resume> findByMemberId(Long id) throws Exception {
		Resume args = new Resume();
		args.setMemberId(id);
		
		// 種別、年、月で並べ替えて検索する
		List<Resume> resumes = template.query(
				"SELECT id, typ, member_id, ym, content FROM resumes WHERE member_id = :memberId ORDER BY typ, ym",
				new BeanPropertySqlParameterSource(args),
				new BeanPropertyRowMapper<>(Resume.class));

		return resumes;
	}

	/**
	 * IDを検索条件にresumesテーブルを検索する
	 * @param id ID
	 * @return
	 * @throws Exception
	 */
	public Resume findById(Long id) throws Exception {
		Resume args = new Resume();
		args.setId(id);
		
		//一つだけ検索される
		Resume resume = template.queryForObject(
				"SELECT id, typ, member_id, ym, content FROM resumes WHERE id = :id",
				new BeanPropertySqlParameterSource(args),
				new BeanPropertyRowMapper<>(Resume.class));

		return resume;
	}

	/**
	 * 経歴と紐づく会員を検索する
	 * @param id ID
	 * @return
	 * @throws Exception
	 */
	public MemberResume findWithMemberById(Long id) throws Exception {
		Resume args = new Resume();
		//プレースホルダの設定
		args.setId(id);
		//経歴と紐づく会員の取得
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT");
		sb.append("   mem.name");
		sb.append("   , mem.id AS member_id");
		sb.append("   , res.id AS resume_id");
		sb.append("   , res.typ");
		sb.append("   , res.ym");
		sb.append("   , res.content");
		sb.append(" FROM resumes res");
		sb.append("   INNER JOIN members mem ON");
		sb.append("   res.member_id = mem.id");
		sb.append(" WHERE");
		sb.append("   res.id = :id");
		MemberResume memberResume = template.queryForObject(
				sb.toString(),
				new BeanPropertySqlParameterSource(args),
				new BeanPropertyRowMapper<>(MemberResume.class)); //戻り値の型

		return memberResume;
	}
	
	/**
	 * レコード挿入
	 * @param resume 経歴エンティティ
	 * @throws Exception
	 */
	public void insert(Resume resume) throws Exception {
		template.update(
				"INSERT INTO resumes(typ, ym, content, member_id) VALUES(:typ, :ym, :content, :memberId)",
				new BeanPropertySqlParameterSource(resume));
	}
	
	/**
	 * レコード更新
	 * @param memberResume 経歴エンティティ
	 * @throws Exception
	 */
	public void update(MemberResume memberResume) throws Exception {
		Resume resume = new Resume();
		//更新
		resume.setTyp(memberResume.getTyp());
		resume.setYm(memberResume.getYm());
		resume.setContent(memberResume.getContent());
		resume.setId(memberResume.getResumeId());
		
		template.update(
				"UPDATE resumes SET typ = :typ, ym = :ym, content = :content WHERE id = :id",
				new BeanPropertySqlParameterSource(resume));
	}
	
	/**
	 * レコード削除
	 * @param id ID
	 * @throws Exception
	 */
	public void delete(Long id) throws Exception {
		Resume resume = new Resume();
		//削除
		resume.setId(id);
		template.update(
				"DELETE FROM resumes WHERE id = :id",
				new BeanPropertySqlParameterSource(resume));
	}
	
	/**
	 * 会員IDを条件にレコードを削除する
	 * @param memberId 会員ID
	 * @throws Exception
	 */
	public void deleteByMemberId(Long memberId) throws Exception {
		Resume resume = new Resume();
		//削除
		resume.setMemberId(memberId);
		template.update(
				"DELETE FROM resumes WHERE member_id = :memberId",
				new BeanPropertySqlParameterSource(resume));
		
	}
}
