package org.jf.Penroser;

import android.graphics.RectF;

import javax.microedition.khronos.opengles.GL11;

public abstract class HalfRhombus {
    public static final int LEFT=0;
    public static final int RIGHT=1;

    public static final int LOWER_EDGE=0;
    public static final int UPPER_EDGE=1;
    public static final int INNER_EDGE=2;

    public static final int LOWER_EDGE_MASK=1;
    public static final int UPPER_EDGE_MASK=2;
    public static final int INNER_EDGE_MASK=3;

    /*This is the number of levels to use for the static vbo data*/
    /*package*/ static final int VBO_LEVEL=6;

    /**
     * A relative level number. Children have a higher level, parents have a lower level. A negative level is allowed,
     * for example, in the case of upwards generation
     */
    protected int level;

    /** Whether this is the right or left side of a rhombus */
    protected int side;

    /** The x coordinate of the bottom vertex */
    protected float x;

    /** The y coordinate of the bottom vertex */
    protected float y;

    /** The scale of the half rhombus */
    protected float scale;

    /** The rotation of the half rhombus, in increments of 18 degrees */
    protected int rotation;

    /** A rect object that represents the envelope of this half rhombus */
    private RectF envelope = new RectF();
    private boolean envelopeValid = false;

    /** The vertices of this half rhombus */
    private float[] vertices = new float[6];
    private boolean verticesValid = false;

    protected HalfRhombus() {
    }

    protected HalfRhombus(int level, int side, float x, float y, float scale, int rotation) {
        this.level = level;
        this.side = side;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = MathUtil.positiveMod(rotation, 20);
        envelopeValid = false;
        verticesValid = false;
    }

    public void set(int level, int side, float x, float y, float scale, int rotation) {
        this.level = level;
        this.side = side;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = MathUtil.positiveMod(rotation, 20);
        envelopeValid = false;
        verticesValid = false;
    }

    public float getRotationInDegrees() {
        return rotation * 18;
    }

    public abstract int draw(GL11 gl, RectF viewportEnvelope, int maxLevel);
    protected abstract int draw(GL11 gl, RectF viewportEnvelope, int maxLevel, boolean checkIntersect);
    public abstract HalfRhombus getChild(int i);
    protected abstract void calculateVertices(float[] vertices);
    protected abstract void calculateEnvelope(RectF envelope);
    public abstract int getRandomParentType(int edge);
    public abstract HalfRhombus getParent(int parentType);

    protected int oppositeSide() {
        return side==RIGHT?LEFT:RIGHT;
    }

    protected static int oppositeSide(int side) {
        return side==RIGHT?LEFT:RIGHT;
    }

    public float[] getVertices() {
        if (!verticesValid) {
            calculateVertices(vertices);
            verticesValid = true;
        }

        return vertices;
    }

    public RectF getEnvelope() {
        if (!envelopeValid) {
            calculateEnvelope(envelope);
            envelopeValid = true;
        }

        return envelope;
    }

    public boolean envelopeIntersects(RectF viewportEnvelope) {
        return RectF.intersects(getEnvelope(), viewportEnvelope);
    }

    public boolean envelopeCoveredBy(RectF viewportEnvelope) {
        return viewportEnvelope.contains(getEnvelope());
    }

    /**
     * Finds the edges of this half rhombus that intersect with the given viewport. This can be used to detect when the
     * viewport has extended past one or more of the top-most half rhombus's edges

     * @param viewport The viewport to check against
     * @return A bitmask indicating which edges intersect. The bitmask consists of the LOWER_EDGE_MASK, UPPER_EDGE_MASK
     * and INNER_EDGE_MASK values
     */
    public int getIntersectingEdges(float[] viewport) {
        assert viewport.length == 8;

        float[] triangle = getVertices();
        int edgeMask = GeometryUtil.triangleRectIntersection(triangle, viewport);
        return edgeMask;
    }
}