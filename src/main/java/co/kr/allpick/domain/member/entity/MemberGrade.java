package co.kr.allpick.domain.member.entity;

public enum MemberGrade {

    NORMAL("일반", 0),
    SILVER("실버", 300_000),
    GOLD("골드", 1_000_000),
    PLATINUM("플래티넘", 3_000_000);

    private final String displayName;
    private final int minAmount;

    MemberGrade(String displayName, int minAmount) {
        this.displayName = displayName;
        this.minAmount = minAmount;
    }

    public String getDisplayName() { return displayName; }
    public int getMinAmount() { return minAmount; }

    public static MemberGrade from(long totalAmount) {
        if (totalAmount >= PLATINUM.minAmount) return PLATINUM;
        if (totalAmount >= GOLD.minAmount)     return GOLD;
        if (totalAmount >= SILVER.minAmount)   return SILVER;
        return NORMAL;
    }
}