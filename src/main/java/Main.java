import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

public class Main {
    public static void main(String[] strings){
       PlatformConfiguration config = PlatformConfiguration.getDefault();
       config.addComponent("tool.testBDI.class");
       //config.addComponent("voter.VoterBDI.class");
       //config.addComponent("voter.testBDI.class");

//        config.addComponent("voter.VoterBDI.class");
        Starter.createPlatform().get();
    }
}
