package jaci.openrio.toast.lib.registry;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The Registrar is a simple class designed to get rid of Allocation exceptions in code, where if multiple
 * instances of a Motor Controller, DIO, or Analog interface are created with the same port, WPILib throws
 * a big fat error in your face. To solve this, the Registrar keeps one instance of each when you request it,
 * allowing for multiple accesses, but not multiple constructions.
 *
 * @param <ID>      The type to use for the Identifier. In most cases, this is an integer.
 * @param <Type>    The type to store in this registrar. This is the superclass
 */
public class Registrar<ID, Type> {

    private volatile HashMap<ID, Type> registered = new HashMap<>();

    /**
     * Fetch an object from the Registrar, or create it if it doesn't exist yet.
     *
     * @param id        The ID to store the object under
     * @param clazz     The Class (Type) of the object you are inserting (subclass of Type)
     * @param creator   The lambda function that will construct a new instance of the object if it doesn't
     *                  already exist in the registrar
     * @param <T>       The type to insert / fetch from the registrar. This is a subclass of the Registrar's Type
     * @return          The object in the registrar / the new object created.
     */
    public synchronized  <T extends Type> T fetch(ID id, Class<T> clazz, Supplier<T> creator) {
        Type in_register = registered.get(id);
        if (in_register == null) {
            T instance = creator.get();
            registered.put(id, instance);
            return instance;
        } else {
            if (clazz.isAssignableFrom(in_register.getClass()))
                return (T) in_register;
            else
                throw new IllegalStateException("An object already exists in the Registrar with the given ID, " +
                        "but is of a different type! Expected: " + clazz.getName() + ", but got: " + in_register.getClass().getName());
        }
    }

    /**
     * Return the stream of the underlying Registrar hashmap. Use this to directly manipulate the registrar
     */
    public synchronized Stream<Map.Entry<ID, Type>> stream() {
        return registered.entrySet().stream();
    }

    // -- STATICS -- //

    public static volatile Registrar<Integer, PWM>              pwmRegistrar    = new Registrar<>();
    public static volatile Registrar<Integer, SpeedController>  canRegistrar    = new Registrar<>();
    public static volatile Registrar<Integer, DigitalSource>    dioRegistrar    = new Registrar<>();
    public static volatile Registrar<Integer, AnalogInput>      aiRegistrar     = new Registrar<>();
    public static volatile Registrar<Integer, AnalogOutput>     aoRegistrar     = new Registrar<>();

    /**
     * Get a DigitalOutput instance from the Registrar
     * @param port the DIO port to use
     */
    public static DigitalOutput digitalOutput(int port) {
        return dioRegistrar.fetch(port, DigitalOutput.class, () -> { return new DigitalOutput(port); });
    }

    /**
     * Get a DigitalInput instance from the Registrar
     * @param port the DIO port to use
     */
    public static DigitalInput digitalInput(int port) {
        return dioRegistrar.fetch(port, DigitalInput.class, () -> { return new DigitalInput(port); });
    }

    /**
     * Get an AnalogOutput instance from the Registrar
     * @param port the AO port to use
     */
    public static AnalogOutput analogOutput(int port) {
        return aoRegistrar.fetch(port, AnalogOutput.class, () -> { return new AnalogOutput(port); });
    }

    /**
     * Get an AnalogInput instance from the Registrar
     * @param port the AI port to use
     */
    public static AnalogInput analogInput(int port) {
        return aiRegistrar.fetch(port, AnalogInput.class, () -> { return new AnalogInput(port); });
    }

    // -- Motor Controllers -- //

    /**
     * Get a Talon [SR] instance from the Registrar
     * @param pwmPort the PWM Port to use
     */
    public static Talon talon(int pwmPort) {
        return pwmRegistrar.fetch(pwmPort, Talon.class, () -> { return new Talon(pwmPort); });
    }

    /**
     * Get a Talon SRX [PWM] instance from the Registrar
     * @param pwmPort the PWM Port to use
     */
    public static TalonSRX talonSRX(int pwmPort) {
        return pwmRegistrar.fetch(pwmPort, TalonSRX.class, () -> { return new TalonSRX(pwmPort); });
    }

    /**
     * Get a Jaguar [PWM] instance from the Registrar
     * @param pwmPort the PWM port to use
     */
    public static Jaguar jaguar(int pwmPort) {
        return pwmRegistrar.fetch(pwmPort, Jaguar.class, () -> { return new Jaguar(pwmPort); });
    }

    /**
     * Get a Victor instance from the Registrar
     * @param pwmPort the PWM port to use
     */
    public static Victor victor(int pwmPort) {
        return pwmRegistrar.fetch(pwmPort, Victor.class, () -> { return new Victor(pwmPort); });
    }

    /**
     * Get a Victor SP instance from the Registrar
     * @param pwmPort the PWM port to use
     */
    public static VictorSP victorSP(int pwmPort) {
        return pwmRegistrar.fetch(pwmPort, VictorSP.class, () -> { return new VictorSP(pwmPort); });
    }

    /**
     * Get a Talon SRX [CAN] instance from the Registrar
     * @param canID the CAN Device ID to use
     */
    public static CANTalon canTalon(int canID) {
        return canRegistrar.fetch(canID, CANTalon.class, () -> { return new CANTalon(canID); });
    }
}
