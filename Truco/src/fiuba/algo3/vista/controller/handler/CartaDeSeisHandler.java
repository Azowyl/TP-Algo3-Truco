package fiuba.algo3.vista.controller.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fiuba.algo3.modelo.Carta;
import fiuba.algo3.modelo.Jugador;
import fiuba.algo3.modelo.excepciones.AccionInvalidaException;
import fiuba.algo3.modelo.tipoJuego.JuegoTruco;
import fiuba.algo3.vista.controller.Controller;
import fiuba.algo3.vista.controller.MesaController;
import fiuba.algo3.vista.controller.MesaDeSeisController;
import fiuba.algo3.vista.controller.MesaGeneralController;
import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CartaDeSeisHandler extends CartaHandlerGeneral {

	private boolean fuePicaPica = false;
	private static int picaPicaJugando = 0;
	public CartaDeSeisHandler(List<ImageView> cartasDeMano, List<List<ImageView>> cartasJugando,
			Carta cartaQueRepresenta, Label puntosEquipoUno, Label puntosEquipoDos) {
		super(cartasDeMano, cartasJugando, cartaQueRepresenta, puntosEquipoUno, puntosEquipoDos);
	}
	
	private void borrarManosActualesYPicaPica() {
		JuegoTruco juegoTruco = Controller.juegoTruco.getEnfrentamientoActual();
		int posicionJugador = juegoTruco.obtenerMesa()
				.getJugadores().indexOf(juegoTruco.jugadorDeTurno())+picaPicaJugando;
		int posicionRival;
		if(posicionJugador%2==0) {
			posicionRival = posicionJugador+1;
		} else {
			posicionRival = posicionJugador-1;
		}
		List<ImageView> cartasQueVuelvenAlMazo = new ArrayList<>();
		cartasQueVuelvenAlMazo.addAll(cartasEnJuego.get(posicionJugador));
		cartasQueVuelvenAlMazo.addAll(cartasEnJuego.get(posicionRival));
		for(ImageView carta : cartasQueVuelvenAlMazo) {
			carta.setVisible(false);
		}
		picaPicaJugando+=2;
	}


	
	public void actualizarConPicaPica() {

		JuegoTruco juegoTruco = Controller.juegoTruco;
		if (juegoTruco.esPicaPica()) 
		{		for(List<ImageView> imagesView :cartasEnJuego) {
			for(ImageView imageView : imagesView) {
				mostrarDorso(imageView);
			}
		}
			if (!juegoTruco.getEnfrentamientoActual().manoFinalizada()) {
				habilitarCartasPicaPica();
				
			} else {
				limpiarContenedores();
				borrarManosActualesYPicaPica();
				juegoTruco.terminarEnfrentamiento();
				lblEquipoUno.setText(String.valueOf(juegoTruco.puntosEquipoUno()));
				lblEquipoDos.setText(String.valueOf(juegoTruco.puntosEquipoDos()));
				juegoTruco.cambiarEnfrentamiento();
				habilitarSiguientePicaPica();
				fuePicaPica = true;
			}
		} else {
			Controller.juegoTruco.jugadorDeTurnoJuegaCarta(this.cartaQueSoy);
			if(!juegoTruco.esPicaPica()) {
				habilitarCartas();
			}
			actualizar();
			fuePicaPica = false;
		
		}
		if(!juegoTruco.esPicaPica() && fuePicaPica){
			restaurarMesa();
			picaPicaJugando=0;
		}
	}
	
	
	@Override
	public void handle(Event event) {
		try {
			JuegoTruco juegoTruco = Controller.juegoTruco;
			ImageView cartaJugada = (ImageView) event.getSource();
			cartaJugada.setVisible(false);
			jugarCarta(cartaJugada);
			
			if (juegoTruco.esPicaPica()) {
				juegoTruco.crearEnfrentamientosPicaPica();
				juegoTruco.getEnfrentamientoActual().jugadorDeTurnoJuegaCarta(this.cartaQueSoy);
			}
			actualizarConPicaPica();
			

		} catch (AccionInvalidaException exception) {
			System.out.println("no se puede jugar carta"); // esto es temporal
		}
	}
	
	private void restaurarMesa() {
		int posicionJugadorMano = Controller.juegoTruco.obtenerMesa()
				.getJugadores().indexOf(Controller.juegoTruco.jugadorDeTurno());
		int posicionJugadorActual = 0;
		for(List<ImageView> mano : cartasEnJuego) {
			for(ImageView carta : mano) {
				if(posicionJugadorActual == posicionJugadorMano) {
					mostrarCarta(carta);
				} else {
					mostrarDorso(carta);
				}
				carta.setVisible(true);
				
			}
			posicionJugadorActual++;
		}
	}

	public void habilitarSiguientePicaPica() {
		JuegoTruco juegoTruco = Controller.juegoTruco;
		Jugador jugador = juegoTruco.getEnfrentamientoActual().jugadorDeTurno();
		int posicionJugador = juegoTruco.obtenerMesa().getJugadores().indexOf(jugador);
		List<ImageView> cartas = cartasEnJuego.get(posicionJugador);
		for(ImageView carta : cartas) {
			mostrarCarta(carta);
		}
	}

	private void habilitarCartasPicaPica() {
		int posicionJugador = Controller.juegoTruco.obtenerMesa().getJugadores()
				.indexOf(Controller.juegoTruco.getEnfrentamientoActual().jugadorDeTurno());

		for (ImageView carta : cartasDeMano) {
			mostrarDorso(carta);
		}

		List<ImageView> manoAHabilitar = cartasEnJuego.get(posicionJugador);

		for (ImageView carta : manoAHabilitar) {
			mostrarCarta(carta);
		}

	}

	private void habilitarCartas() {
		List<ImageView> cartasAHabilitar = new ArrayList<ImageView>();
		cartasAHabilitar.addAll(cartasDeMano);

		int posicionDeManoSiguiente = MesaController.obtenerManosIntercaladas()
				.indexOf(Controller.juegoTruco.jugadorDeTurno().getMano());
		List<ImageView> cartasSiguientes = this.cartasEnJuego.get(posicionDeManoSiguiente);

		cartasAHabilitar.addAll(cartasSiguientes);
		for (ImageView carta : cartasAHabilitar) {
			if (!carta.isDisable()) {
				mostrarDorso(carta);
			} else {
				mostrarCarta(carta);
				carta.setDisable(false);
			}

		}
	}

	private void jugarCarta(ImageView cartaJugada) {
		Image imagenCarta = cartaJugada.getImage();
		contenedorAsociado.setImage(imagenCarta);
	}
}
