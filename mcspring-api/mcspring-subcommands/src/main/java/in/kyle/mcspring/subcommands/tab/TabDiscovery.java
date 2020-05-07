package in.kyle.mcspring.subcommands.tab;

//@Component
//@RequiredArgsConstructor
//public class TabDiscovery {
//
//    private final SimpleMethodInjection injection;
//
//    public List<String> getCompletions(CommandSender sender,
//                                       String commandString,
//                                       Consumer<PluginCommand> consumer) {
//        if (!commandString.isEmpty() && !commandString.endsWith(" ")) {
//            // TODO: 2020-02-25 Enable partial completions
//            return Collections.emptyList();
//        }
//        var parts = new ArrayList<>(Arrays.asList(commandString.split(" ")));
//        if (parts.get(0).isEmpty()) {
//            parts.remove(0);
//        }
//        var command = new PluginCommandTabCompletable(injection, sender, parts);
//        consumer.accept(command);
//
//        return getCompletions(command);
//    }
//
//    private List<String> getCompletions(PluginCommandTabCompletable command) {
//        if (command.hasChild()) {
//            return getCompletions(command.getChild());
//        } else if (command.getState() == State.MISSING_ARG || command.getState() == State.CLEAN) {
//            return command.getTabCompletionOptions();
//        } else {
//            return Collections.emptyList();
//        }
//    }
//}
