package Design;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public  class CustomLabel extends JLabel
{
    private boolean selected;
    private static final Color selectionColor = new Color ( 82, 158, 202 );
    private CustomData data;
     private ImageIcon tipIcon;
    public CustomLabel(ImageIcon tipIcon)
    {
        super ();
        this.tipIcon=tipIcon;
        setOpaque ( false );
        setBorder ( BorderFactory.createEmptyBorder ( 0, 36 + 5, 0, 40 ) );
    }
    public void setSelected ( boolean selected )
    {
        this.selected = selected;
        setForeground ( selected ? Color.WHITE : Color.BLACK );
    }

    public void setData ( CustomData data )
    {
        this.data = data;
        setText ( data.getName () );
    }

    @Override
    public void paintComponent ( Graphics g ) {
        Graphics2D g2d = ( Graphics2D ) g;
        g2d.setRenderingHint ( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        if ( selected )
        {
            Area area = new Area ( new Ellipse2D.Double ( 0, 0, 36, 36 ) );
            area.add ( new Area ( new RoundRectangle2D.Double ( 18, 3, getWidth () - 18, 29, 6, 6 ) ) );
            g2d.setPaint ( selectionColor );
            g2d.fill ( area );

            g2d.setPaint ( Color.LIGHT_GRAY );
         g2d.fill ( new Ellipse2D.Double ( 2, 2, 32, 32 ) );
        }
       g2d.setPaint(Color.WHITE);
        g2d.fill ( new Ellipse2D.Double ( 5, 5, 26, 26 ) );
        g2d.drawImage ( tipIcon.getImage (), 5 + 13 - tipIcon.getIconWidth () / 2, 5 + 13 - tipIcon.getIconHeight () / 2, null );
        System.out.println(tipIcon.getIconWidth());
      if ( data.getNewMessages () > 0 )
        {
            g2d.setPaint (Color.red);
            g2d.fill ( new Ellipse2D.Double ( getWidth () - 18 - 5, getHeight () / 2 - 9, 18, 18 ) );

            final String text = "" + data.getNewMessages ();
            final Font oldFont = g2d.getFont ();
            g2d.setFont ( oldFont.deriveFont ( oldFont.getSize () - 1f ) );
            final FontMetrics fm = g2d.getFontMetrics ();
            g2d.setPaint ( Color.WHITE );
            g2d.drawString ( text, getWidth () - 9 - 5 - fm.stringWidth ( text ) / 2,
                    getHeight () / 2 + ( fm.getAscent () - fm.getLeading () - fm.getDescent () ) / 2 );
            g2d.setFont ( oldFont );
        }

        super.paintComponent ( g );
    }

    @Override
    public Dimension getPreferredSize ()
    {
        final Dimension ps = super.getPreferredSize ();
        ps.height = 36;
        return ps;
    }
}
