package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;

import java.util.Random;

public class TipNotice {

    public static String getNotice() {
        Random rand = new Random();
        int noticeNumber = rand.nextInt(0, 9);
        return switch (noticeNumber) {
            case 0 -> "§f 摩 " + format("#fcd07f") + "알고 계셨나요? 점프를 두 번 하여 더블 점프를 할 수 있습니다!";
            case 1 -> "§f 摩 " + format("#FFF897") + "알고 계셨나요? 아무것도 들지 않고, SHIFT+F를 누르면 메뉴를 열 수 있습니다! §7§o(/메뉴)";
            case 2 -> "§f 摩 " + format("#FF97AC") + "전직의 증서를 획득하고, 자신의 직업무기 +MAX를 강화하여 전직할 수 있습니다! §7§o(/강화)";
            case 3 -> "§f 摩 " + format("#97FFB7") + "각 마을에는 서브퀘스트가 있습니다! 사냥의 보상을 극대화 해보세요!";
            case 4 ->
                    "§f 摩 " + format("#97EEFF") + "직업 변경 시에는 해당 직업의 레벨로 변경됩니다. 다시 직업을 돌아오면 그대로 저장되어 있으니 걱정하지 마세요! §7§o(/직업)";
            case 5 ->
                    "§f 摩 " + format("#FFD4FC") + "문제가 발생하여 고객센터를 통해 복구받은 경우, 메일함을 통해 복구를 받을 수 있습니다. 문의가 종료되었다면 메일함을 확인해보세요! §7§o(/메일함)";
            case 6 -> "§f 摩 " + format("#8193FF") + "아이템을 소모하여 도감을 채우면 영구적인 스탯 보상을 얻을 수 있습니다! 각 특수무기의 +MAX 등급을 인벤토리에 가지고 있으면 표시됩니다! §7§o(/도감)";
            case 7 -> "§f 摩 " + format("#FFCF4E") + "IDE 온라인에서는 시장을 통한 거래만 가능합니다. 아쉽게도 개인 거래는 불가합니다. §7§o(/시장)";
            case 8 -> "§f 摩 " + format("#FFC770") + "시장에 아이템을 팔고 싶은데 시세를 모르겠나요? 판매 할 아이템을 손에 들고 /시세 명령어를 입력하면 최근 거래 내역을 확인할 수 있습니다! §7§o(/시세)";
            default -> "";
        };
    }

    private static String format(String hex) {
        return ColorManager.format(hex);
    }
}
