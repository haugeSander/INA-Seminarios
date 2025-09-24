package dispositivo.componentes;

import dispositivo.api.mqtt.FuncionPublisher_APIMQTT;
import dispositivo.interfaces.FuncionStatus;
import dispositivo.interfaces.IFuncion;
import dispositivo.utils.MySimpleLogger;

public class Funcion implements IFuncion {
	private FuncionPublisher_APIMQTT publisher;
	protected String id = null;
	protected String deviceId = null;

	protected FuncionStatus initialStatus = null;
	protected FuncionStatus status = null;

	private boolean habilitado = true;
	
	private String loggerId = null;
	
	public static Funcion build(String id, String deviceId, FuncionPublisher_APIMQTT publisher) {
		return new Funcion(id, FuncionStatus.OFF, deviceId, publisher);
	}
	
	public static Funcion build(String id, FuncionStatus initialStatus, String deviceId, FuncionPublisher_APIMQTT publisher) {
		return new Funcion(id, initialStatus, deviceId, publisher);
	}

	protected Funcion(String id, FuncionStatus initialStatus, String deviceId, FuncionPublisher_APIMQTT publisher) {
		this.id = id;
		this.initialStatus = initialStatus;
    	this.deviceId = deviceId;
		this.loggerId = "Funcion " + id;
		this.publisher = publisher;
	}
		
	@Override
	public String getId() {
		return this.id;
	}
		
	@Override
	public IFuncion encender() {

		if (!habilitado) return this;
		MySimpleLogger.info(this.loggerId, "==> Encender");
		this.setStatus(FuncionStatus.ON);
		return this;
	}

	@Override
	public IFuncion apagar() {

		MySimpleLogger.info(this.loggerId, "==> Apagar");
		this.setStatus(FuncionStatus.OFF);
		return this;
	}

	@Override
	public IFuncion parpadear() {

		MySimpleLogger.info(this.loggerId, "==> Parpadear");
		this.setStatus(FuncionStatus.BLINK);
		return this;
	}
	
	protected IFuncion _putIntoInitialStatus() {
		switch (this.initialStatus) {
		case ON:
			this.encender();
			break;
		case OFF:
			this.apagar();
			break;
		case BLINK:
			this.parpadear();
			break;

		default:
			break;
		}
		
		return this;

	}

	@Override
	public FuncionStatus getStatus() {
		return this.status;
	}
	
	protected IFuncion setStatus(FuncionStatus status) {
		if (!isHabilitado()) {
			MySimpleLogger.info(loggerId, "Ignored state change to " + status + " because disabled");
        	return this;
		}
		this.status = status;
		if (publisher != null) {
			String topic = String.format("dispositivo/%s/funcion/%s/info", deviceId, id);
			publisher.publish_status(topic, id, status.toString());
		}
		return this;
	}
	
	@Override
	public IFuncion iniciar() {
		this._putIntoInitialStatus();
		return this;
	}
	
	@Override
	public IFuncion detener() {
		return this;
	}

	@Override
	public Boolean habilitar() {
		this.habilitado = true;
		return true;		
	}

	@Override
	public Boolean deshabilitar() {
		this.habilitado = false;
		return true;		
	}

	@Override
	public Boolean isHabilitado() {
		return this.habilitado;
	}
	
	
}
