
package com.minebone.signshopguardian.operations;

import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.operations.SignShopArguments;
import org.wargamer2010.signshop.operations.SignShopOperation;
import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshop.util.signshopUtil;

import com.minebone.signshopguardian.SignShopGuardian;

public class givePlayerGuardians implements SignShopOperation {
    @Override
    public Boolean setupOperation(SignShopArguments ssArgs) {
        if(!SignShopGuardian.isEnabledForWorld(ssArgs.getPlayer().get().getWorld())) {
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("guardian_not_allowed_in_world", ssArgs.getMessageParts()));
            return false;
        }

        ssArgs.setMessagePart("!guardians", signshopUtil.getNumberFromLine(ssArgs.getSign().get(), 1).intValue() + "");
        return true;
    }

    @Override
    public Boolean checkRequirements(SignShopArguments ssArgs, Boolean activeCheck) {
        if(!SignShopGuardian.isEnabledForWorld(ssArgs.getPlayer().get().getWorld())) {
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("guardian_not_allowed_in_world", ssArgs.getMessageParts()));
            return false;
        }

        ssArgs.setMessagePart("!guardians", signshopUtil.getNumberFromLine(ssArgs.getSign().get(), 1).intValue() + "");
        ssArgs.setMessagePart("!currentguardians", SignShopGuardian.getManager().getGuardians(ssArgs.getPlayer().get().getName()) + "");
        return true;
    }

    @Override
    public Boolean runOperation(SignShopArguments ssArgs) {
        SignShopPlayer player = ssArgs.getPlayer().get();
        int totalGuardians = SignShopGuardian.getManager().addGuardians(player.getName(), signshopUtil.getNumberFromLine(ssArgs.getSign().get(), 1).intValue());
        ssArgs.setMessagePart("!guardians", signshopUtil.getNumberFromLine(ssArgs.getSign().get(), 1).intValue() + "");
        ssArgs.setMessagePart("!currentguardians", totalGuardians + "");
        return true;
    }
}
