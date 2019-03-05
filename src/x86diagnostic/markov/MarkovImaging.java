package x86diagnostic.markov;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MarkovImaging {

    private Markov markov;
    public MarkovImaging(Markov m){
        markov = m;
    }

    public BufferedImage render(){
        List<MarkovInstruction> mis = markov.objects();
        int N = mis.size();
        long M = 0;

        for(MarkovInstruction a : mis)
        for(MarkovInstruction b : mis)
            M = Math.max(M,markov.get(a,b));

        BufferedImage img = new BufferedImage(N,N, BufferedImage.TYPE_INT_RGB);

        for(int i=0; i<N; i++)
        for(int j=0; j<N; j++){
            float x = (float)Math.pow(1-(double)markov.get(mis.get(i), mis.get(j)) / M, 2);
            img.setRGB(i,j,new Color(1.0f, x,x).getRGB());
        }

        return img;
    }

    public void output(String file) throws IOException {
        ImageIO.write(render(), "png", new File((file + ".png").replace(".png.png", ".png")));
    }
}
