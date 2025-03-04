package org.prgrms.kdt.shop.repository;

import com.wix.mysql.EmbeddedMysql;
import org.junit.jupiter.api.*;
import org.prgrms.kdt.shop.CommandLineApplication;
import org.prgrms.kdt.shop.domain.FixedAmountVoucher;
import org.prgrms.kdt.shop.domain.PercentDiscountVoucher;
import org.prgrms.kdt.shop.domain.Voucher;
import org.prgrms.kdt.shop.enums.VoucherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v8_0_11;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class VoucherJdbcRepositoryTest {

    @Autowired
    VoucherRepository voucherRepository;
    @MockBean
    CommandLineApplication commandLineApplication;
    EmbeddedMysql embeddedMysql;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create().url("jdbc:mysql://localhost:2215/order_mgmt")
                .username("test").password("test1234!").build();
        }

        @Bean
        VoucherRepository voucherRepository() {
            return new VoucherJdbcRepository(dataSource());
        }
    }

    @BeforeAll
    void setup() {
        var mysqlConfig = aMysqldConfig(v8_0_11).withCharset(UTF8).withPort(2215)
            .withUser("test", "test1234!").withTimeZone("Asia/Seoul").build();
        embeddedMysql = anEmbeddedMysql(mysqlConfig).addSchema("order_mgmt",
            classPathScript("schema.sql")).start();
    }

    @AfterAll
    void cleanup() {
        embeddedMysql.stop();
    }

    @Test
    @DisplayName("입력 테스트")
    void insert() {
        //given
        //when
        voucherRepository.insert(
            new FixedAmountVoucher(UUID.randomUUID(), 1000, LocalDateTime.now()));
        //then
        assertThat(voucherRepository.findAll().isEmpty(), is(false));
    }

    @Test
    @DisplayName("모든 항목 검색 테스트")
    void findAll() {
        //given
        voucherRepository.insert(
            new PercentDiscountVoucher(UUID.randomUUID(), 2000, LocalDateTime.now()));
        //when
        var findList = voucherRepository.findAll();
        //then
        assertThat(findList.size(), is(1));
    }

    @Test
    @DisplayName("ID를 이용한 검색 테스트")
    void findById() {
        //given
        UUID uuid = UUID.randomUUID();
        //when
        var insertVoucher = voucherRepository.insert(
            new PercentDiscountVoucher(uuid, 1500, LocalDateTime.now()));
        var findVoucher = voucherRepository.findById(uuid);
        //then
        assertThat(findVoucher.get().getVoucherId(), is(insertVoucher.getVoucherId()));
        assertThat(findVoucher.get().getVoucherType(), is(insertVoucher.getVoucherType()));
        assertThat(findVoucher.get().getAmount(), is(insertVoucher.getAmount()));
        //given
        //when
        voucherRepository.deleteAll();
        //then
        assertThat(voucherRepository.findById(UUID.randomUUID()), is(Optional.empty()));
    }

    @Test
    @DisplayName("특정 항목 삭제 테스트")
    void deleteTest() {
        //given
        var uuid = UUID.randomUUID();
        voucherRepository.insert(new FixedAmountVoucher(uuid, 1000, LocalDateTime.now()));
        //when
        voucherRepository.delete(uuid);
        //then
        assertThat(voucherRepository.findAll().size(), is(0));

    }

    @Test
    @DisplayName("모든 항목 삭제 테스트")
    void deleteAll() {
        //given
        voucherRepository.insert(
            new FixedAmountVoucher(UUID.randomUUID(), 1000, LocalDateTime.now()));
        //when
        voucherRepository.deleteAll();
        //then
        assertThat(voucherRepository.findAll().isEmpty(), is(true));
    }

    @Test
    @DisplayName("바우처 타입으로 찾기 테스트")
    void findByType() {
        //given
        voucherRepository.insert(
            new FixedAmountVoucher(UUID.randomUUID(), 1000, LocalDateTime.now()));
        voucherRepository.insert(
            new PercentDiscountVoucher(UUID.randomUUID(), 10, LocalDateTime.now()));
        //when
        List<Voucher> voucherList = voucherRepository.findByType(VoucherType.FIXED_AMOUNT);
        //then
        assertThat(voucherList.size(), is(1));
    }
}