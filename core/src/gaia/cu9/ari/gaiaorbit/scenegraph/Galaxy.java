package gaia.cu9.ari.gaiaorbit.scenegraph;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import gaia.cu9.ari.gaiaorbit.GaiaSky;
import gaia.cu9.ari.gaiaorbit.render.IRenderable;
import gaia.cu9.ari.gaiaorbit.render.SceneGraphRenderer;
import gaia.cu9.ari.gaiaorbit.util.Constants;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.concurrent.ThreadIndexer;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;
import gaia.cu9.ari.gaiaorbit.util.time.ITimeFrameProvider;

public class Galaxy extends Particle {

    public Galaxy(Vector3d pos, float appmag, float absmag, float colorbv, String name, float ra, float dec,
	    long starid) {
	super(pos, appmag, absmag, colorbv, name, ra, dec, starid);
    }

    @Override
    public double THRESHOLD_NONE() {
	return (float) 0;
    }

    @Override
    public double THRESHOLD_POINT() {
	return (float) 4E-10;
    }

    @Override
    public double THRESHOLD_QUAD() {
	return (float) 1.7E-12;
    }

    @Override
    protected void setDerivedAttributes() {
	double flux = Math.pow(10, -absmag / 2.5f);
	setRGB(colorbv);

	// Calculate size - This contains arbitrary boundary values to make
	// things nice on the render side
	size = (float) (Math.min((Math.pow(flux, 0.5f) * Constants.PC_TO_U * 0.16f), 1e9f) * 2.5e0d);
	computedSize = 0;
    }

    /**
     * Re-implementation of update method of {@link CelestialBody} and
     * {@link SceneGraphNode}.
     */
    @Override
    public void update(ITimeFrameProvider time, final Transform parentTransform, ICamera camera, float opacity) {
	if (appmag <= GlobalConf.runtime.LIMIT_MAG_RUNTIME) {
	    TH_OVER_FACTOR = (float) (THRESHOLD_POINT() / GlobalConf.scene.LABEL_NUMBER_FACTOR);
	    transform.position.set(parentTransform.position).add(pos);
	    distToCamera = transform.position.len();

	    this.opacity = opacity;

	    if (!copy) {
		// addToRender(this, RenderGroup.POINT_GAL);

		viewAngle = (radius / distToCamera) / camera.getFovFactor();
		viewAngleApparent = viewAngle * GlobalConf.scene.STAR_BRIGHTNESS;

		addToRenderLists(camera);
	    }

	}
    }

    protected boolean addToRender(IRenderable renderable, RenderGroup rg) {
	SceneGraphRenderer.render_lists.get(rg).add(renderable, ThreadIndexer.i());
	return true;
    }

    @Override
    protected void addToRenderLists(ICamera camera) {
	if (camera.getCurrent() instanceof FovCamera) {
	    // Render as point, do nothing
	} else {
	    addToRender(this, RenderGroup.SHADER_GAL);
	}
	if (renderText() && camera.isVisible(GaiaSky.instance.time, this)) {
	    addToRender(this, RenderGroup.LABEL);
	}

    }

    @Override
    public void render(ShaderProgram shader, float alpha, boolean colorTransit, Mesh mesh, ICamera camera) {
	compalpha = alpha;

	float size = getFuzzyRenderSize(camera);

	Vector3 aux = aux3f1.get();
	shader.setUniformf("u_pos", transform.getTranslationf(aux));
	shader.setUniformf("u_size", size);

	float[] col = colorTransit ? ccTransit : ccPale;
	shader.setUniformf("u_color", col[0], col[1], col[2], alpha * opacity);
	shader.setUniformf("u_distance", (float) distToCamera);
	shader.setUniformf("u_apparent_angle", (float) viewAngleApparent);

	shader.setUniformf("u_radius", getRadius());

	// Sprite.render
	mesh.render(shader, GL20.GL_TRIANGLES, 0, 6);
    }

    @Override
    protected float labelFactor() {
	return 1e1f;
    }

    @Override
    protected float labelMax() {
	return 0.00005f;
    }

    @Override
    public float textSize() {
	return super.textSize();
    }

}
