package com.resumejdbc.repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.resumejdbc.entity.Member;

@Repository
public class MemberNamedParamJdbcRepository {

	private NamedParameterJdbcTemplate template;

	/**
	 * コンストラクタ
	 * @param template NamedParameterJdbcTemplate
	 */
	public MemberNamedParamJdbcRepository(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	/**
	 * 誕生日を条件にmembersテーブルを検索
	 * @param from 検索開始日
	 * @param to 検索終了日
	 * @return　検索結果
	 * @throws Exception
	 */
	public List<Member> findByBirth(LocalDate from, LocalDate to) throws Exception {
		Map<String, Object> args = new HashMap<>();
		
		StringBuilder sql = new StringBuilder();
		//以降のif文を満たした時にANDで繋ぐために「WHERE 1=1」を使う(他の条件に影響は与えない)
		sql.append("SELECT id, name, birth, email FROM members WHERE 1=1");
		
		//from-toの両方が指定された場合は「birth BETWEEN :fromBirth AND :toBirth」と同じ意味になる
		if(Objects.nonNull(from)) {
			sql.append(" AND birth >= :fromBirth");
			//プレースホルダの設定
			args.put("fromBirth", from);
		}
		if(Objects.nonNull(to)) {
			sql.append(" AND birth <= :toBirth");
			//プレースホルダの設定
			args.put("toBirth", to);
		}
		
		sql.append(" ORDER BY id");
		
		List<Member> members = template.query(
				sql.toString(),
				args, 
				new BeanPropertyRowMapper<>(Member.class));
		
		return members;
	}
	
	/**
	 * IDを検索条件にmembersテーブルを検索する
	 * @param id ID
	 * @return　検索結果
	 * @throws Exception
	 */
	public Member findById(Long id) throws Exception {
		Map<String, Object> args = new HashMap<>();
		//プレースホルダの設定
		args.put("id", id);
		Member member = template.queryForObject(
				"SELECT id, name, birth, email FROM members WHERE id = :id",
				args,
				new BeanPropertyRowMapper<>(Member.class)); //戻り値の型

		return member;
	}
	
	/**
	 * メアドを条件に一致したレコード数を返す
	 * @param email メアド
	 * @param excludeId 検索時に除外する会員ID
	 * @return 件数
	 * @throws Exception
	 */
	public long countByEmail(String email, Long excludeId) throws Exception {
		Map<String, Object> args = new HashMap<>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM members WHERE email = :email");
		//プレースホルダの設定
		args.put("email", email);
		
		if(Objects.nonNull(excludeId)) {
			//検索対象外のIDが指定されている
			sql.append(" AND id != :excludeId");
			args.put("excludeId", excludeId);
		}
		
		Long count = template.queryForObject(
				sql.toString(),
				args,
				Long.class); //戻り値の型
		return count;
	}
	
	/**
	 * レコード挿入
	 * @param member 会員エンティティ
	 * @throws Exception
	 */
	public void insert(Member member) throws Exception {
		Map<String, Object> args = new HashMap<>();
		//新規登録 プレースホルダの順番にaddすること
		args.put("name", member.getName());
		args.put("birth", member.getBirth());
		args.put("email", member.getEmail());
		template.update(
				"INSERT INTO members(name, birth, email) VALUES(:name, :birth, :email)",
				args);
	}

	/**
	 * レコード更新
	 * @param member 会員エンティティ
	 * @throws Exception
	 */
	public void update(Member member) throws Exception {
		Map<String, Object> args = new HashMap<>();
		//更新 プレースホルダの順番にaddすること
		args.put("name", member.getName());
		args.put("birth", member.getBirth());
		args.put("email", member.getEmail());
		args.put("id", member.getId());
		template.update(
				"UPDATE members SET name = :name, birth = :birth, email = :email WHERE id = :id",
				args);
	}
	
	/**
	 * レコード削除
	 * @param id ID
	 * @throws Exception
	 */
	public void delete(Long id) throws Exception {
		Map<String, Object> args = new HashMap<>();
		//削除
		args.put("id", id);
		template.update(
				"DELETE FROM members WHERE id = :id",
				args);
		
	}
	
}
