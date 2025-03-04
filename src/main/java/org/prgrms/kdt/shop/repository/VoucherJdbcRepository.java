package org.prgrms.kdt.shop.repository;

import org.prgrms.kdt.shop.domain.FixedAmountVoucher;
import org.prgrms.kdt.shop.domain.PercentDiscountVoucher;
import org.prgrms.kdt.shop.domain.Voucher;
import org.prgrms.kdt.shop.enums.VoucherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VoucherJdbcRepository implements VoucherRepository {

    private static final Logger logger = LoggerFactory.getLogger(VoucherJdbcRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public VoucherJdbcRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final RowMapper<Voucher> voucherRowMapper = (resultSet, i) -> {
        var voucherId = toUUID(resultSet.getBytes("voucher_id"));
        var voucherAmount = resultSet.getInt("voucher_amount");
        var voucherType = resultSet.getString("voucher_type");
        if (VoucherType.find(voucherType).equals(VoucherType.FIXED_AMOUNT)) {
            return new FixedAmountVoucher(voucherId, voucherAmount, LocalDateTime.now());
        } else if (VoucherType.find(voucherType).equals(VoucherType.PERCENT_DISCOUNT)) {
            return new PercentDiscountVoucher(voucherId, voucherAmount, LocalDateTime.now());
        }
        return null;
    };

    @Override
    public List<Voucher> findAll() {
        String sql = "select * from vouchers";
        return jdbcTemplate.query(sql, voucherRowMapper);
    }

    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        String sql = "select * from vouchers WHERE voucher_id = UUID_TO_BIN(?)";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, voucherRowMapper,
                voucherId.toString().getBytes()));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
        }
        return Optional.empty();
    }

    @Override
    public Voucher insert(Voucher voucher) {
        String sql = "insert into vouchers(voucher_id, voucher_amount, voucher_type, created_at) values(UUID_TO_BIN(?),?,?,?)";
        var insert = jdbcTemplate.update(sql, voucher.getVoucherId().toString().getBytes(),
            voucher.getAmount(), voucher.getVoucherType().getInputVoucher(),
            voucher.getCreatedAt());
        if (insert != 1) {
            logger.error("Nothing was inserted");
            throw new IllegalArgumentException("Nothing was inserted");
        }
        return voucher;
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from vouchers");
    }

    @Override
    public void delete(UUID voucherId) {
        String sql = "delete from vouchers where voucher_id = UUID_TO_BIN(?)";
        try {
            jdbcTemplate.update(sql, voucherId.toString().getBytes());
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
        }
    }

    @Override
    public List<Voucher> findByType(VoucherType voucherType) {
        String sql = "select * from vouchers WHERE voucher_type = ?";
        return jdbcTemplate.query(sql, voucherRowMapper, voucherType.getInputVoucher());
    }

    private static UUID toUUID(byte[] bytes) {
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
