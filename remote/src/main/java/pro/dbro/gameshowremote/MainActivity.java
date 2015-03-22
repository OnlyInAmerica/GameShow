package pro.dbro.gameshowremote;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import pro.dbro.airshare.app.AirShareService;
import pro.dbro.airshare.app.IncomingTransfer;
import pro.dbro.airshare.app.OutgoingTransfer;
import pro.dbro.airshare.app.ui.AirShareActivity;
import pro.dbro.airshare.session.Peer;
import pro.dbro.airshare.transport.Transport;
import timber.log.Timber;


public class MainActivity extends AirShareActivity implements AirShareActivity.AirShareCallback,
                                                              WelcomeFragment.WelcomeFragmentListener,
                                                              ControllerFragment.ControllerFragmentListener,
                                                              AirShareService.AirSharePeerCallback,
                                                              AirShareService.AirShareReceiverCallback,
                                                              AirShareService.AirShareSenderCallback {

    AirShareService.ServiceBinder serviceBinder;
    Peer host;

    TextView gameStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAirShareCallback(this);

        gameStatus = (TextView) findViewById(R.id.gameStatus);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showWelcomeFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.frame, new WelcomeFragment())
                .commit();
    }

    public void showControllerFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.frame, new ControllerFragment())
                .commit();
    }

    @Override
    public void registrationRequired() {
        Timber.d("registrationRequired");
        showWelcomeFragment();
    }

    @Override
    public void onServiceReady(AirShareService.ServiceBinder serviceBinder) {
        Timber.d("onServiceReady");

        this.serviceBinder = serviceBinder;

        serviceBinder.scanForOtherUsers();
        serviceBinder.setPeerCallback(this);
        serviceBinder.setReceiverCallback(this);
        serviceBinder.setSenderCallback(this);

        showControllerFragment();
    }

    @Override
    public void onUsernameSelected(String username) {
        registerUserForService(username, "Jeoparty");
    }

    @Override
    public void peerStatusUpdated(Peer peer, Transport.ConnectionStatus newStatus) {
        Toast.makeText(this, peer.getAlias() + " is " + newStatus.toString(), Toast.LENGTH_SHORT).show();
        if (newStatus == Transport.ConnectionStatus.CONNECTED) {
            host = peer;
            gameStatus.setText(R.string.connected);
        }
        else if (newStatus == Transport.ConnectionStatus.DISCONNECTED) {
            host = null;
            gameStatus.setText(R.string.not_connected);
        }
    }

    @Override
    public void onTransferOffered(IncomingTransfer transfer, Peer sender) {

    }

    @Override
    public void onTransferProgress(IncomingTransfer transfer, Peer sender, float progress) {

    }

    @Override
    public void onTransferComplete(IncomingTransfer transfer, Peer sender, Exception exception) {
    }

    @Override
    public void onTransferOfferResponse(OutgoingTransfer transfer, Peer recipient, boolean recipientDidAccept) {

    }

    @Override
    public void onTransferProgress(OutgoingTransfer transfer, Peer recipient, float progress) {

    }

    @Override
    public void onTransferComplete(OutgoingTransfer transfer, Peer recipient, Exception exception) {

    }

    @Override
    public void onButtonPushed() {
        Timber.d("Button pressed");
        if (serviceBinder != null && host != null) {
            serviceBinder.offer("Button".getBytes(), host);
        }
    }
}
