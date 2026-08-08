package org.swlab.etcetera.Training.commands;

import com.binggre.binggreapi.command.BetterCommand;
import com.binggre.binggreapi.command.CommandArgument;
import org.swlab.etcetera.Training.commands.arguments.admin.*;

import java.util.List;

public class AdminCommand extends BetterCommand {

    @Override
    public String getCommand() {
        return "훈련관리";
    }

    @Override
    public boolean isSingleCommand() {
        return false;
    }

    @Override
    public List<CommandArgument> getArguments() {
        return List.of(new ReloadArgument(),
                new CreateArgument(),
                new SaveArgument(),
                new LocationArgument(),
                new EntityLocationArgument(),
                new DeleteArgument());
    }
}
