package com.sjy.book_sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.sjy.book_sys.model.RentDto;
import com.sjy.book_sys.model.RentListResDto;

/**
 * 도서 대출, 반납 Mapper
 * @author 신지영
 * @since 2023.11.10
 */
@Mapper
public interface RentMapper {

	/**
	 * 대여 신청 Mapper
	 * @param rentDto
	 */
	@Insert("insert into RENT (book_id, member_id) values (#{bookId}, #{memberId})")
	public int insertRent(RentDto rentDto);
	
	/**
	 * 대여 가능 여부 조회 Mapper
	 * @param rentDto
	 * @return 대여 가능시 null, 불가능시 expected_return_dt 
	 */
	@Select("select expected_return_dt from RENT where book_id=#{bookId} and member_id=#{memberId} and return_dt is null")
	public String isSameBook(RentDto rentDto);
	
	/**
	 * member 도서 대여 권수 추가
	 * @param rentDto
	 */
	@Update("update MEMBER set member_rent_cnt = member_rent_cnt + 1 where member_id=#{memberId}")
	public int updateMemberRentCnt(RentDto rentDto);
	
	/**
	 * book 대출 권수 추가
	 * @param rentDto
	 */
	@Update("update BOOK set book_rental_cnt = book_rental_cnt + 1 where book_id=#{bookId}")
	public int updateBookRentalCnt(RentDto rentDto);
	
	/**
	 * member 도서 대여 가능 권수 조회
	 * @param memberId
	 */
	@Select("select member_rent_cnt from MEMBER where member_id=#{memberId}")
	public int findRentCnt(String memberId);
	
	/**
	 * 전체 대출 이력 조회
	 * @return List<RentListResDto>
	 */
	@Select("select b.book_name, b.book_id, r.rent_id, r.member_id, r.rent_dt, r.return_dt, r.expected_return_dt from RENT r inner join BOOK b on b.book_id = r.book_id order by rent_dt desc")
	public List<RentListResDto> findAllRentList();
	
	/**
	 * bookId에 대한 대출이력 조회
	 * @param bookId
	 * @return List<RentListResDto>
	 */
	@Select("select b.book_name, b.book_id, r.rent_id, r.member_id, r.rent_dt, r.return_dt, r.expected_return_dt from RENT r inner join BOOK b on b.book_id = r.book_id where book_Name like #{bookName} order by rent_dt desc")
	public List<RentListResDto> findRentListById(String BookName);
	
	/**
	 * 도서 반납 (반납일 등록) mapper
	 * @param rentId
	 * @return 성공시 1
	 */
	@Update("update RENT set return_dt = sysdate() where rent_id=#{rentId}")
	public int returnBook(String rentId);
	
	/**
	 * 회원 도서 대출 권수 감소
	 * @param memberId
	 * @return 성공시 1
	 */
	@Update("update MEMBER set member_rent_cnt = member_rent_cnt-1 where member_id=#{memberId}")
	public int decreaseMemberRentCnt(String memberId);
	
	/**
	 * 도서 대출 권수 감소
	 * @param bookId
	 * @return 성공시 1
	 */
	@Update("update BOOK set book_rental_cnt = book_rental_cnt-1 where book_id=#{bookId}")
	public int decreaseBookRentalCnt(String bookId);
	
	@Select("select expected_return_dt from RENT where return_dt is null and expected_return_dt < SYSDATE() and member_id=#{memberId}")
	public String isOverdue(String memberId);
}
